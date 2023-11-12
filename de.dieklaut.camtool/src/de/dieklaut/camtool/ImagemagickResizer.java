package de.dieklaut.camtool;

import java.nio.file.Path;

import org.apache.commons.exec.CommandLine;

import de.dieklaut.camtool.external.ExternalTool;
import de.dieklaut.camtool.util.ImageResizer;

public class ImagemagickResizer implements ImageResizer {

	@Override
	public boolean resize(int maxDimension, Path sourceFile, Path destinationFile, int qualityPercentage) {
		CommandLine commandlineIdentify = new CommandLine("identify");
		commandlineIdentify.addArgument("-ping");
		commandlineIdentify.addArgument("-format");
		commandlineIdentify.addArgument("%w %h", false);
		commandlineIdentify.addArgument(sourceFile.toAbsolutePath().toString(), false);
		
		ExternalTool identify = new ExternalTool() {
			
			@Override
			public CommandLine getCommandLine() {
				return commandlineIdentify;
			}
		};
		
		if (identify.process(true)) {	
			String [] output = identify.getOutput().split(" ");
			int w = Integer.parseInt(output[0]);
			int h = Integer.parseInt(output[1]);
			if (w < h) {
				w = maxDimension;
			} else {
				h = maxDimension;
			}
			
			CommandLine commandline = new CommandLine("convert");
			commandline.addArgument(sourceFile.toAbsolutePath().toString(), false);
			commandline.addArgument("-auto-orient");
			commandline.addArgument("-strip");
			commandline.addArgument("-quality");
			commandline.addArgument(Integer.toString(qualityPercentage), false);
			if (maxDimension > 0) {
				commandline.addArgument("-resize");
				commandline.addArgument(w + "x" + h + ">");
				
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
		return false;
	}

}
