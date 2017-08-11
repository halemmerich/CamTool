package de.dieklaut.camtool;

import java.nio.file.Path;

public class FileTypeHelper {
	
	private static String [] rawFileSuffixes = new String [] {"arw", "nef"};
	
	public static boolean isRawImageFile(Path path) {
		for (String current : rawFileSuffixes) {
			if (path.getFileName().toString().toLowerCase().endsWith(current)) {
				return true;
			}
		}
		return false;
	}
}
