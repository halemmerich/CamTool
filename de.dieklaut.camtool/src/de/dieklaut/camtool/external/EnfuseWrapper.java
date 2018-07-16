package de.dieklaut.camtool.external;

import java.nio.file.Path;

import org.apache.commons.exec.CommandLine;

public class EnfuseWrapper extends ExternalTool {
	
	private Path [] inputFilePaths;
	private Path outputFilePath;
	
	public void setInputFiles(Path ... inputFilePath) {
		this.inputFilePaths = inputFilePath;
	}
	
	public void setOutputFile(Path outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
	
	@Override
	public CommandLine getCommandLine() {
		CommandLine commandline = new CommandLine("enfuse");
		commandline.addArgument("-o");
		commandline.addArgument(outputFilePath.toAbsolutePath().normalize().toString(), false);

		for (Path current : inputFilePaths) {
			commandline.addArgument(current.toAbsolutePath().normalize().toString(), false);
		}
		
		return commandline;
	}

}
