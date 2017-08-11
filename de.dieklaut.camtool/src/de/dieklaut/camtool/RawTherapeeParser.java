package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class RawTherapeeParser {

	public static boolean isDeleted(Path element) {
		Properties profile = new Properties();
		try {
			profile.load(Files.newInputStream(element));
			return Boolean.parseBoolean(profile.getProperty("InTrash"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
