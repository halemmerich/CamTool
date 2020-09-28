package de.dieklaut.camtool;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import de.dieklaut.camtool.Logger.Level;

public class RawTherapeeParser {

	public static boolean isDeleted(Path element) {
		Properties profile = new Properties();
		try (InputStream stream = Files.newInputStream(element)){
			profile.load(stream);
			return Boolean.parseBoolean(profile.getProperty("InTrash"));
		} catch (IOException e) {
			Logger.log("Failure while parsing RawTherapee ini", e, Level.WARNING);
		}
		return false;
	}

	public static String get(Path file, String propertyName) {
		Properties profile = new Properties();
		try (InputStream stream = Files.newInputStream(file)){
			profile.load(stream);
			return profile.getProperty(propertyName);
		} catch (IOException e) {
			Logger.log("Failure while parsing RawTherapee ini", e, Level.WARNING);
		}
		return null;
	}

}
