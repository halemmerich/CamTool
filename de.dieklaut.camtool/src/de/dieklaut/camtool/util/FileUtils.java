package de.dieklaut.camtool.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

import de.dieklaut.camtool.FileOperationException;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;

public class FileUtils {
	public static Instant getCreationDate(Path filePath) throws FileOperationException {
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(Files.newInputStream(filePath));

			Collection<ExifIFD0Directory> directories = metadata.getDirectoriesOfType(ExifIFD0Directory.class);
			
			for (ExifIFD0Directory directory : directories) {
				Date date = directory.getDate(ExifIFD0Directory.TAG_DATETIME);
				if (date != null) {
					return date.toInstant();
				}
			}
		} catch (ImageProcessingException | IOException e) {
			Logger.log("Could not parse image file exif data for a creation date, falling back to file creation date", e, Level.DEBUG);
		}
		
		try {
			return Files.readAttributes(filePath, BasicFileAttributes.class).creationTime().toInstant();
		} catch (IOException e) {
			throw new FileOperationException("Could not get the creation date from file " + filePath, e);
		}
	}
	
	public static void deleteRecursive(Path path) throws FileOperationException {
		File candidate = path.toFile();

		if (candidate.delete()) {
			return;
		}
		
		try {
			Files.list(path).forEach(file -> {
				try {
					deleteRecursive(file);
				} catch (FileOperationException e) {
					Logger.log("Error during delete", e);
				}
			});
		} catch (IOException e) {
			throw new FileOperationException("Could get list of files for " + path, e);
		}
		
		//Delete directory if empty
		try (DirectoryStream<Path> directoryCandidate = Files.newDirectoryStream(path)){
			if (!directoryCandidate.iterator().hasNext()){
				candidate.delete();
				return;
			}
		} catch (IOException e) {
			throw new FileOperationException("Could not delete directory " + path, e);
		}
	}
	
	public static String getTimestamp(Instant instant) {
		return Long.toString(instant.toEpochMilli());
		
	}
}
