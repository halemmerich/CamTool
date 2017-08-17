package de.dieklaut.camtool;

import java.util.LinkedList;
import java.util.List;

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
	public String getCommandLine() {
		String commandline = "rawtherapee-cli";
		commandline += " -o " + '"' + outputFilePath + '"';
		commandline += " -j" + quality;
		commandline += " -js" + subsampling;

		commandline += " -d";
		for (String current : profileOptions) {
			commandline += " -p " + '"' + current + '"';
		}
		
		commandline += " -c " + '"' + inputFilePath + '"';
		return commandline;
	}

}
