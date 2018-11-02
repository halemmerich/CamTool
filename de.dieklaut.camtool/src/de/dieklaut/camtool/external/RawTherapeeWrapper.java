package de.dieklaut.camtool.external;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.exec.CommandLine;

public class RawTherapeeWrapper extends ExternalTool {
	
	public enum FileType {
		TIFF_16_COMPRESSED, PNG_8, JPG
	}
	
	String commandLine = "rawtherapee-cli";
	private List<String> profileOptions = new LinkedList<>();
	private int quality;
	private int subsampling;
	private String inputFilePath;
	private String outputFilePath;
	private FileType type = FileType.PNG_8;
	
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
	
	public void setOutputFileType(FileType type) {
		this.type = type;
	}
	
	@Override
	public CommandLine getCommandLine() {
		CommandLine commandline = new CommandLine("rawtherapee-cli");
		
		switch (type) {
		case JPG:
			commandline.addArgument("-j" + Integer.toString(quality));
			commandline.addArgument("-js" + Integer.toString(subsampling));
			break;
		case PNG_8:
			commandline.addArgument("-n");
			commandline.addArgument("-b8");
			break;
		case TIFF_16_COMPRESSED:
			commandline.addArgument("-tz");
			commandline.addArgument("-b16");
			break;
		}
		
		//Overwrite output if present
		commandline.addArgument("-Y");
		
		commandline.addArgument("-o");
		commandline.addArgument(outputFilePath, false);

		//Use default configured profile
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
