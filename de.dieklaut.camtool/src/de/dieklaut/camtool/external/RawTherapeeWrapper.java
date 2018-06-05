package de.dieklaut.camtool.external;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.exec.CommandLine;

public class RawTherapeeWrapper extends ExternalTool {
	
	String commandLine = "rawtherapee-cli";
	private List<String> profileOptions = new LinkedList<>();
	private int quality;
	private int subsampling;
	private String inputFilePath;
	private String outputFilePath;
	
	public void addProfileOption(String profileFile) {
		this.profileOptions.add(profileFile);
	}
	
	public void setInputFile(String inputFilePath) {
		this.inputFilePath = inputFilePath;
	}
	
	public void setOutputFile(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
	
	public void setJpgQuality(int quality, int subsampling) {
		this.quality = quality;
		this.subsampling = subsampling;
	}
	
	@Override
	public CommandLine getCommandLine() {
		CommandLine commandline = new CommandLine("rawtherapee-cli");
		commandline.addArgument("-o");
		commandline.addArgument(outputFilePath, false);
		commandline.addArgument("-j" + Integer.toString(quality));
		commandline.addArgument("-js" + Integer.toString(subsampling));

		commandline.addArgument("-d");
		for (String current : profileOptions) {
			commandline.addArgument("-p");
			commandline.addArgument(current, false);
		}
		
		commandline.addArgument("-c");
		commandline.addArgument(inputFilePath, false);
		return commandline;
	}

}
