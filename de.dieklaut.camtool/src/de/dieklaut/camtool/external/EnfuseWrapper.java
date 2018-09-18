package de.dieklaut.camtool.external;

import java.nio.file.Path;
import java.util.Locale;

import org.apache.commons.exec.CommandLine;

public class EnfuseWrapper extends ExternalTool {
	
	private Path [] inputFilePaths;
	private Path outputFilePath;
	
	private double exposureWeight = -1;
	private double saturationWeight = -1;
	
	private double exposureOptimum = -1;
	private double exposureWidth = -1;
	private double contrastWeight = -1;
	private boolean useHardMask = false;
	
	public void setInputFiles(Path ... inputFilePath) {
		this.inputFilePaths = inputFilePath;
	}
	
	public void setOutputFile(Path outputFilePath) {
		this.outputFilePath = outputFilePath;
	}
	
	public void setExposureWeight(double exposureWeight) {
		this.exposureWeight = exposureWeight;
	}

	public void setSaturationWeight(double saturationWeight) {
		this.saturationWeight = saturationWeight;
	}

	public void setExposureOptimum(double exposureOptimum) {
		this.exposureOptimum = exposureOptimum;
	}

	public void setExposureWidth(double exposureWidth) {
		this.exposureWidth = exposureWidth;
	}

	@Override
	public CommandLine getCommandLine() {
		CommandLine commandline = new CommandLine("enfuse");
		commandline.addArgument("-o");
		commandline.addArgument(outputFilePath.toAbsolutePath().normalize().toString(), false);

		if (exposureWeight != -1) {
			commandline.addArgument("--exposure-weight=" + String.format(Locale.US, "%.2f", exposureWeight));
		}
		if (saturationWeight != -1) {
			commandline.addArgument("--saturation-weight=" + String.format(Locale.US, "%.2f", saturationWeight));
		}
		if (exposureOptimum != -1) {
			commandline.addArgument("--exposure-optimum=" + String.format(Locale.US, "%.2f", exposureOptimum));
		}
		if (exposureWidth != -1) {
			commandline.addArgument("--exposure-width=" + String.format(Locale.US, "%.2f", exposureWidth));
		}
		if (contrastWeight != -1) {
			commandline.addArgument("--contrast-weight=" + String.format(Locale.US, "%.2f", contrastWeight));
		}
		if (useHardMask) {
			commandline.addArgument("--hard-mask");
		}
		
		for (Path current : inputFilePaths) {
			commandline.addArgument(current.toAbsolutePath().normalize().toString(), false);
		}
		
		return commandline;
	}

	public void setContrastWeight(double contrastWeight) {
		this.contrastWeight = contrastWeight;
	}

	public void setHardMask(boolean useHardMask) {
		this.useHardMask  = useHardMask;
	}

}
