package de.dieklaut.camtool;

import java.nio.file.Path;

public class FileTypeHelper {

	private static String [] rawFileSuffixes = new String [] {"arw", "nef"};
	private static String [] videoFileSuffixes = new String [] {"mp4", "mpg", "avi", "avchd", "mkv"};
	
	private static boolean endsWithOne(Path path, String ... strings) {
		for (String current : strings) {
			if (path.getFileName().toString().toLowerCase().endsWith(current)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isRawImageFile(Path path) {
		return endsWithOne(path, rawFileSuffixes);
	}
	
	public static boolean isVideoFile(Path path) {
		return endsWithOne(path, videoFileSuffixes);
	}

	public static boolean isRawTherapeeProfile(Path path) {
		return endsWithOne(path, "pp3");
	}
}
