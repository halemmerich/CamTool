package de.dieklaut.camtool.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collection;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
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
			Logger.log("Could not parse image file exif data for a creation date, falling back to file creation date",
					e, Level.DEBUG);
		}

		try {
			return Files.readAttributes(filePath, BasicFileAttributes.class).lastModifiedTime().toInstant();
		} catch (IOException e) {
			throw new FileOperationException("Could not get the creation date from file " + filePath, e);
		}
	}

	public static Duration getCreationDuration(Path filePath) {
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(Files.newInputStream(filePath));

			Collection<ExifIFD0Directory> directories = metadata.getDirectoriesOfType(ExifIFD0Directory.class);

			for (ExifIFD0Directory directory : directories) {
				double shutterspeed = directory.getDouble(ExifIFD0Directory.TAG_SHUTTER_SPEED);
				Duration.ofNanos((long) (shutterspeed * 1000000));
			}
		} catch (ImageProcessingException | IOException | MetadataException e) {
			Logger.log("Could not parse image file exif data for a creation date, falling back to file creation date",
					e, Level.DEBUG);
		}
		
		return Duration.ZERO;
	}

	/**
	 * @param path
	 *            the {@link Path} to be deleted
	 * @param force
	 *            set to true, if read only files should be deleted
	 * @throws FileOperationException
	 */
	public static void deleteRecursive(Path path, boolean force) throws FileOperationException {
		deleteRecursive(path, path, force);
	}
	
	private static void deleteRecursive(Path path, Path originalPath, boolean force) throws FileOperationException {
		File candidate = path.toFile();
		
		if (force && !Files.isSymbolicLink(path)) {
			candidate.setWritable(true);
		}

		if (candidate.delete()) {
			return;
		}

		try {
			Files.list(path).forEach(file -> {
				try {
					deleteRecursive(file, originalPath, force);
				} catch (FileOperationException e) {
					Logger.log("Error during delete", e);
				}
			});
		} catch (IOException e) {
			throw new FileOperationException("Could get list of files for " + path, e);
		}

		// Delete directory if empty
		try (DirectoryStream<Path> directoryCandidate = Files.newDirectoryStream(path)) {
			if (!directoryCandidate.iterator().hasNext()) {
				candidate.delete();
				return;
			}
		} catch (IOException e) {
			throw new FileOperationException("Could not delete directory " + path, e);
		}
	}

	public static String getTimestamp(Instant instant) {
		DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
		builder.appendPattern("yyyyMMddhhmmssSSS");
		DateTimeFormatter formatter = builder.toFormatter();
		formatter = formatter.withZone(ZoneOffset.ofHours(0));
		return formatter.format(instant);
	}
	
	public static String getTimestamp(Path file) throws FileOperationException {
		return getTimestamp(getCreationDate(file));
	}

	public static Path moveSymlink(Path current, Path destination) throws IOException {
		Path symlinkTarget = destination.relativize(current.toRealPath());
		Path newSymlink = destination.resolve(current.getFileName());
		Path newLink = Files.createSymbolicLink(newSymlink, symlinkTarget);
		Files.delete(current);
		return newLink;
	}

	public static long getTimestampPortion(Path current) {
		String filename = current.getFileName().toString();
		return Long.parseLong(filename.substring(0, filename.indexOf('_')));
	}

	public static String getGroupName(Path current) {
		return getGroupName(current.getFileName().toString());
	}

	public static String getGroupName(String filename) {
		return filename.substring(0, filename.indexOf('.'));
	}

	public static String getNamePortion(Path current) {
		return getNamePortion(current.getFileName().toString());
	}

	public static String getNamePortion(String filename) {
		return filename.substring(filename.indexOf('_'));
	}

	public static String removeSuffix(String fileName) {
		if (!fileName.contains(".")) {
			throw new IllegalArgumentException("The given file name contains no '.', hence no suffix to remove");
		}
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}
}
