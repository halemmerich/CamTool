package de.dieklaut.camtool.external;

import java.nio.file.Path;

import org.apache.commons.exec.CommandLine;

public class ConvertWrapper extends ExternalTool {
	
	private Path inputFilePath;
	private Path outputFilePath;
	
	public void setInputFile(Path  inputFilePath) {
		this.inputFilePath = inputFilePath;
	}
	
	public void setOutputFile(Path outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
	
	@Override
	public CommandLine getCommandLine() {
		CommandLine commandline = new CommandLine("convert");
		commandline.addArgument(inputFilePath.toAbsolutePath().toString(), false);
		commandline.addArgument("-quality");
		commandline.addArgument(Integer.toString(95), false);
		commandline.addArgument(outputFilePath.toAbsolutePath().toString(), false);
		return commandline;
	}

}
