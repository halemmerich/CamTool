package de.dieklaut.camtool;

import java.nio.file.Path;

public interface ExternalTool {
	public boolean process(Path destinationFile, Path ... inputFiles);
}
