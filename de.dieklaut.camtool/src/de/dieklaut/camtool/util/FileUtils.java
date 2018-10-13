package de.dieklaut.camtool.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
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
import com.drew.metadata.exif.ExifSubIFDDirectory;

import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;

public class FileUtils {
	public static Instant getCreationDate(Path filePath) {
		try (InputStream stream = Files.newInputStream(filePath)){
			Metadata metadata = ImageMetadataReader.readMetadata(stream);

			Collection<ExifIFD0Directory> directories = metadata.getDirectoriesOfType(ExifIFD0Directory.class);

			for (ExifIFD0Directory directory : directories) {
				Date date = directory.getDate(ExifIFD0Directory.TAG_DATETIME);
				if (date != null) {
					return date.toInstant();
				}
			}
		} catch (ImageProcessingException | IOException e) {
			Logger.log("Error during parsing of image file " + filePath + " for exif data for a creation date, falling back to file creation date",
					e, Level.DEBUG);
		}

		try {
			Logger.log("No date found in image file " + filePath + " exif data, falling back to file creation date", Level.INFO);
			return Files.readAttributes(filePath, BasicFileAttributes.class).lastModifiedTime().toInstant();
		} catch (IOException e) {
			throw new IllegalStateException("Could not get the creation date from file " + filePath, e);
		}
	}

	public static Duration getCreationDuration(Path filePath) {
		try (InputStream stream = Files.newInputStream(filePath)){
			Metadata metadata = ImageMetadataReader.readMetadata(stream);

			Collection<ExifSubIFDDirectory> directories = metadata.getDirectoriesOfType(ExifSubIFDDirectory.class);

			for (ExifSubIFDDirectory directory : directories) {
				if (directory.containsTag(ExifSubIFDDirectory.TAG_SHUTTER_SPEED)) {
					double shutterspeed = directory.getDouble(ExifIFD0Directory.TAG_SHUTTER_SPEED);
					Duration result = Duration.ofNanos((long) (shutterspeed * 1000000));

					Logger.log("Found creation duration for file " + filePath + " " + result, Level.DEBUG);
					
					return result;
				}
				if (directory.containsTag(ExifSubIFDDirectory.TAG_EXPOSURE_TIME)) {
					double shutterspeed = directory.getDouble(ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
					Duration result = Duration.ofNanos((long) (shutterspeed * 1000000000));

					Logger.log("Found creation duration for file " + filePath + " " + result, Level.DEBUG);
					
					return result;
				}
			}
			
			
		} catch (ImageProcessingException | IOException | MetadataException e) {
			Logger.log("Could not parse image file exif data for a shutter duration, falling back to duration 0",
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
	public static void deleteRecursive(Path path, boolean force) {
		File candidate = path.toFile();
		
		if (force && !Files.isSymbolicLink(path)) {
			candidate.setWritable(true);
		}
		
		if (Files.isDirectory(path)) {
			try {
				Files.list(path).forEach(file -> deleteRecursive(file, force));
			} catch (IOException e) {
				throw new IllegalStateException("Could get delete contents of " + path + " recursive", e);
			}
			
			// Delete directory if empty
			try (DirectoryStream<Path> directoryCandidate = Files.newDirectoryStream(path)) {
				if (!directoryCandidate.iterator().hasNext()) {
					candidate.delete();
					return;
				}
			} catch (IOException e) {
				throw new IllegalStateException("Could not delete directory " + path, e);
			}
		} else {
			try {
				Path parent = path.toAbsolutePath().getParent();
				boolean writeable = parent.toFile().canWrite();
				if (force) {
					parent.toFile().setWritable(true);
				}
				Files.delete(path);
				if (force) {
					parent.toFile().setWritable(writeable);
				}
			} catch (IOException e) {
				Logger.log("Error during delete of " + path, e);
			}
		}

		
	}

	public static void copyRecursive(Path source, Path destination) throws IOException {
		if (Files.isDirectory(source)) {
			if (!Files.isDirectory(destination)) {
				throw new IllegalArgumentException("Both arguments must be directories");
			}
			Files.list(source).forEach(current -> {
				try {
					copyRecursive(current, destination.resolve(current.getFileName()));
				} catch (IOException e) {
					throw new IllegalStateException(
							"Could not copy recursively from " + current + " to " + destination);
				}
			});
		} else {
			Files.copy(source, destination);
		}
	}
	
	public static String getTimestamp(long epoch) {
		return getTimestamp(Instant.ofEpochMilli(epoch));
	}

	public static String getTimestamp(Instant instant) {
		DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
		builder.appendPattern("yyyyMMddHHmmssSSS");
		DateTimeFormatter formatter = builder.toFormatter();
		formatter = formatter.withZone(ZoneOffset.ofHours(0));
		return formatter.format(instant);
	}

	public static String getTimestamp(Path file) {
		return getTimestamp(getCreationDate(file));
	}

	public static Path moveSymlink(Path current, Path destination) throws IOException {
		Path currentTarget = Files.readSymbolicLink(current);
		
		if (!Files.exists(destination)) {
			Files.createDirectories(destination);
		}

		Path symlinkTarget = null;
		if (currentTarget.isAbsolute()) {
			symlinkTarget = destination.relativize(currentTarget);
		} else {
			symlinkTarget = current.getParent().resolve(currentTarget).toRealPath(LinkOption.NOFOLLOW_LINKS);
		}
		
		Path newSymlink = destination.toRealPath(LinkOption.NOFOLLOW_LINKS).resolve(current.getFileName());
		
		Path relativeTarget = symlinkTarget;
		if (relativeTarget.isAbsolute()) {
			relativeTarget = newSymlink.getParent().relativize(symlinkTarget);
		}
		
		Path newLink = Files.createSymbolicLink(newSymlink, relativeTarget);
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
		return filename.contains(".") ? filename.substring(0, filename.indexOf('.')) : filename;
	}

	public static String getNamePortion(Path current) {
		return getNamePortion(current.getFileName().toString());
	}

	public static String getNamePortion(String filename) {
		if (filename.contains(".")) {
			filename = filename.substring(0, filename.indexOf("."));
		}
		if (filename.contains("_")) {
			return filename.substring(filename.indexOf('_') + 1);
		}
		return filename;
	}

	public static String removeSuffix(String fileName) {
		if (!fileName.contains(".")) {
			throw new IllegalArgumentException("The given file name contains no '.', hence no suffix to remove");
		}
		return fileName.substring(0, fileName.lastIndexOf('.'));
	}

	public static void cleanUpEmptyParents(Path current) {
		Path parent = current.getParent();
		try {
			if (Files.list(parent).count() == 0) {
				Files.delete(parent);
				cleanUpEmptyParents(parent);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Listing parents content failed", e);
		}
	}

	public static void setReadOnlyRecursive(Path path) {
		if (Files.isDirectory(path)) {
			try {
				Files.list(path).forEach(current -> {setReadOnlyRecursive(current);});
			} catch (IOException e) {
				Logger.log("Error while setting " + path + " to readonly", e);
			}
		}
		path.toFile().setReadOnly();
	}

	public static String getSimplifiedStringRep(Path path) {
		return path.toString().replaceAll(path.getFileSystem().getSeparator(), "");
	}

	public static String buildFileName(String timestamp, Path relativePath, String name) {
		return timestamp + "_" + FileUtils.getSimplifiedStringRep(relativePath) + "-" + name;
	}

	public static String buildFileName(long epoch, String name) {
		return buildFileName(getTimestamp(epoch), name);
	}

	public static String buildFileName(String timestamp, String name) {
		return timestamp + "_" + name;
	}

	public static void removeEmptyFolders(Path sortingFolder) throws IOException {
		Files.list(sortingFolder).filter(current -> {
			try {
				return Files.isDirectory(current) && Files.list(current).count() == 0;
			} catch (IOException e) {
				Logger.log("Failure during listing of contents for " + current, Level.ERROR);
				return false;
			}
		});
	}
}
