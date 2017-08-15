package de.dieklaut.camtool.util;

import java.nio.file.Path;

public interface VideoResizer {
	public boolean resize(int maxDimension, Path sourceFile, Path destinationFile, int qualityPercentage);
}
