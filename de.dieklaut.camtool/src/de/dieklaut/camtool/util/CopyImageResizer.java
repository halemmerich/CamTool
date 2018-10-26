package de.dieklaut.camtool.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CopyImageResizer implements ImageResizer {

	@Override
	public boolean resize(int maxDimension, Path sourceFile, Path destinationFile, int qualityPercentage) {
		try {
			Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new IllegalStateException("Null resize (copy) of file " + sourceFile + " failed", e);
		}
		return true;
	}

}
