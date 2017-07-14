package de.dieklaut.camtool;

import java.nio.file.Path;

public class FileFactory {
	
	private FileFactory() {
		// Prevent instantiation
	}
	
	public static SourceFile getSourceFile(Path pathToFile) {
		String fileNameLowerCase = pathToFile.toFile().getName().toLowerCase();
		
		ImageFile imagefile = new ImageFile(pathToFile);
		
		if (fileNameLowerCase.endsWith(".arw") || fileNameLowerCase.endsWith(".nef")) {
			
		}
		return null;
	}
}
