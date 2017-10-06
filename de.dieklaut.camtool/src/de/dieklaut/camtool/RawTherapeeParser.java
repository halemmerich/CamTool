package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import de.dieklaut.camtool.Logger.Level;

public class RawTherapeeParser {

	public static boolean isDeleted(Path element) {
		Properties profile = new Properties();
		try {
			profile.load(Files.newInputStream(element));
			return Boolean.parseBoolean(profile.getProperty("InTrash"));
		} catch (IOException e) {
			Logger.log("Failure while parsing RawTherapee ini", e, Level.WARNING);
		}
		return false;
	}

}
