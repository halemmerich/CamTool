package de.dieklaut.camtool;

import java.nio.file.Path;

public class FileTypeHelper {

	private static String [] rawFileSuffixes = new String [] {"arw", "nef"};
	private static String [] videoFileSuffixes = new String [] {"mp4", "mpg", "avi", "avchd", "mkv", "mts"};
	private static String [] imageFileSuffixes = new String [] {"jpg", "jpeg", "png", "gif", "tif", "tiff"};
	private static String [] vectorFileSuffixes = new String [] {"svg", "pdf"};
	private static String [] renderscriptFileSuffixes = new String [] { Constants.FILE_NAME_RENDERSCRIPT_SUFFIX };
	
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

	public static boolean isRawTherapeeProfile(Path main, Path pp3) {
		return isRawTherapeeProfile(pp3) && main.toFile().getName().startsWith(pp3.toFile().getName().replace(".pp3", ""));
	}

	public static boolean isImageFile(Path file) {
		return endsWithOne(file, imageFileSuffixes);
	}

	public static boolean isRenderscript(Path file) {
		return endsWithOne(file, renderscriptFileSuffixes);
	}

	public static boolean isVectorFile(Path file) {
		return endsWithOne(file, vectorFileSuffixes);
	}

	public static boolean isPdfFile(Path file) {
		return endsWithOne(file, "pdf");
	}
}
