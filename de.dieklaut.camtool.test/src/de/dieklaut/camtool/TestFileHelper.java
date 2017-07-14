package de.dieklaut.camtool;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestFileHelper {
	public static Path getTestResource(String path) {
		return Paths.get(System.getenv("RESOURCES_PATH")).resolve(Paths.get(path));
	}
}
