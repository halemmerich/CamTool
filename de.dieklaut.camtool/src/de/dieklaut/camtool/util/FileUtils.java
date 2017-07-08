package de.dieklaut.camtool.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import de.dieklaut.camtool.Logger;

public class FileUtils {
	public static void deleteRecursive(Path path) throws IOException {
		File candidate = path.toFile();

		if (candidate.isFile()) {
			candidate.delete();
			return;
		}
		
		Files.list(path).forEach(file -> {
			try {
				deleteRecursive(file);
			} catch (IOException e) {
				Logger.log("Error during delete", e);
			}
		});
		
		//Delete directory if empty
		try (DirectoryStream<Path> directoryCandidate = Files.newDirectoryStream(path)){
			if (!directoryCandidate.iterator().hasNext()){
				candidate.delete();
				return;
			}
		}
	}
}
