package de.dieklaut.camtool;

import java.nio.file.Path;

import org.apache.commons.exec.CommandLine;

import de.dieklaut.camtool.external.ExternalTool;
import de.dieklaut.camtool.util.ImageResizer;

public class ImagemagickResizer implements ImageResizer {

	@Override
	public boolean resize(int maxDimension, Path sourceFile, Path destinationFile, int qualityPercentage) {
		CommandLine commandline = new CommandLine("convert");
		commandline.addArgument(sourceFile.toAbsolutePath().toString(), false);
		commandline.addArgument("-auto-orient");
		commandline.addArgument("-strip");
		commandline.addArgument("-quality");
		commandline.addArgument(Integer.toString(qualityPercentage), false);
		if (maxDimension > 0) {
			commandline.addArgument("-resize");
			commandline.addArgument(maxDimension + "x" + maxDimension);
			commandline.addArgument("-unsharp");
			commandline.addArgument("0x0.75+0.75+0.008");
		}
		commandline.addArgument(destinationFile.toAbsolutePath().toString(), false);
		
		ExternalTool convert = new ExternalTool() {
			
			@Override
			public CommandLine getCommandLine() {
				return commandline;
			}
		};
		
		return convert.process();
	}

}
