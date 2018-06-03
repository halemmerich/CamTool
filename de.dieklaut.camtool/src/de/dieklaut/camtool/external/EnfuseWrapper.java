package de.dieklaut.camtool.external;

import org.apache.commons.exec.CommandLine;

public class EnfuseWrapper extends ExternalTool {
	
	private String [] inputFilePaths;
	private String outputFilePath;
	
	public void setInputFile(String ... inputFilePath) {
		this.inputFilePaths = inputFilePath;
	}
	
	public void setOutputFile(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
	
	@Override
	public CommandLine getCommandLine() {
		CommandLine commandline = new CommandLine("enfuse");
		commandline.addArgument("-o");
		commandline.addArgument(outputFilePath, false);

		for (String current : inputFilePaths) {
			commandline.addArgument(current);
		}
		
		return commandline;
	}

}
