package de.dieklaut.camtool;

import java.nio.file.Path;

import org.apache.commons.exec.CommandLine;

import de.dieklaut.camtool.external.ExternalTool;
import de.dieklaut.camtool.util.CopyVideoResizer;
import de.dieklaut.camtool.util.VideoResizer;

public class FfmpegResizer implements VideoResizer {
	
	//ffmpeg -i input-file.mp4 -vf scale=-1:200 output-file.mp4


	/* (non-Javadoc)
	 * @see de.dieklaut.camtool.util.VideoResizer#resize(int, java.nio.file.Path, java.nio.file.Path, int)
	 */
	@Override
	public boolean resize(int maxDimension, Path sourceFile, Path destinationFile, int qualityPercentage) {
		int crf = calculateCrf(qualityPercentage);
		
		CommandLine commandline = new CommandLine("ffmpeg");
		commandline.addArgument("-i", false);
		commandline.addArgument(sourceFile.toAbsolutePath().toString(), false);
		commandline.addArgument("-y"); //Overwrite output
		commandline.addArgument("-vf");
		commandline.addArgument("-crf" + Integer.toString(crf), false);
		if (maxDimension > 0) {
			commandline.addArgument("-filter:v");
			commandline.addArgument("scale=" + maxDimension + ":trunc(ow/a/2)*2", false);
		}
		commandline.addArgument(destinationFile.toAbsolutePath().toString(), false);
		
		ExternalTool ffmpeg = new ExternalTool() {
			
			@Override
			public CommandLine getCommandLine() {
				return commandline;
			}
		};
		
		boolean ffmpegResult = ffmpeg.process();
		
		if (!ffmpegResult) {
			return new CopyVideoResizer().resize(maxDimension, sourceFile, destinationFile, qualityPercentage);
		}
		return ffmpegResult;
	}

	public static int calculateCrf(int qualityPercentage) {		
		int percentageMax = 100;
		int percentageMin = 0;
		
		int targetMin = 18;
		int targetMax = 24;
		
		int percentageRange = percentageMax - percentageMin;
		int targetRange = Math.max(targetMax, targetMin) - Math.min(targetMax, targetMin);

		int reversedQualityPercentage = (qualityPercentage * -1) + percentageMax;
		
		return (((reversedQualityPercentage - percentageMin) * targetRange) / percentageRange) + targetMin;
	}

}
