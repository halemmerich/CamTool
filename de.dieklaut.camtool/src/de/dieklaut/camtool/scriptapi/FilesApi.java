package de.dieklaut.camtool.scriptapi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FilesApi {

	public static void createFile(String path) throws IOException {
		Files.createFile(Paths.get(path));
	}
	
	public static void deleteFile(String path) throws IOException {
		Files.delete(Paths.get(path));
	}
	
	public static void write(String path, byte [] data, boolean append) throws IOException {
		if (append) {
			Files.write(Paths.get(path), data, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		}
	}
	
	public static void write(String path, String data, boolean append) throws IOException {
		if (append) {
			Files.write(Paths.get(path), data.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		}
	}
	
	public static byte [] read(String path) throws IOException {
		return Files.readAllBytes(Paths.get(path));
	}
	
	public static String [] getFiles(String path) throws IOException {
		try (var p = Files.list(Paths.get(path))){
			Object [] paths = p.toArray();
			String [] results = new String [paths.length];
			for (int i = 0; i < paths.length; i++) {
				results[i] = ((Path)paths[i]).toAbsolutePath().toString();
			}
			return results;
		}
	}
	
}
