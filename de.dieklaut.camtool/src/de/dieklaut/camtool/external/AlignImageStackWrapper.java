package de.dieklaut.camtool.external;

import java.nio.file.Path;

import org.apache.commons.exec.CommandLine;

public class AlignImageStackWrapper extends ExternalTool {
	
	private Path [] inputFilePaths;
	private String prefix;
	
	public void setInputFile(Path ... inputFilePath) {
		this.inputFilePaths = inputFilePath;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	@Override
	public CommandLine getCommandLine() {
		CommandLine commandline = new CommandLine("align_image_stack");
		
		if (prefix != null) {
			commandline.addArgument("-a");
			commandline.addArgument(prefix);
		}
		
		//disable default downscaling
		commandline.addArgument("-s");
		commandline.addArgument("0");

		for (Path current : inputFilePaths) {
			commandline.addArgument(current.toAbsolutePath().normalize().toString(), false);
		}
		
		return commandline;
	}

}
