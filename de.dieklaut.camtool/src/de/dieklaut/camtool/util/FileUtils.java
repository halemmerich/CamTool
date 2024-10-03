package de.dieklaut.camtool.util;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;

public class FileUtils {
	
	private static Pattern DATE_FULL_MS = Pattern.compile(".*([0-9]{4}[0-9]{2}[0-9]{2})_([0-9]{2}[0-9]{2}[0-9]{2})_([0-9]{3}).*");
	private static Pattern DATE_FULL = Pattern.compile(".*([0-9]{4}[0-9]{2}[0-9]{2})_([0-9]{2}[0-9]{2}[0-9]{2}).*");

	public static String getCreator(Path filePath) {
		try (InputStream stream = Files.newInputStream(filePath)) {
			Metadata metadata = ImageMetadataReader.readMetadata(stream);

			Collection<ExifIFD0Directory> directories = metadata.getDirectoriesOfType(ExifIFD0Directory.class);

			for (ExifIFD0Directory directory : directories) {
				String model = directory.getString(ExifIFD0Directory.TAG_MODEL);
				if (model != null) {
					return model;
				}
			}
		} catch (ImageProcessingException | IOException e) {
			Logger.log("Error during parsing of image file " + filePath
					+ " for exif data for a creator, falling back to default", e, Level.DEBUG);
		}
		return Constants.UNKNOWN;
	}

	public static Instant getCreationDate(Path filePath) {
		try {
			Long timestamp;
			if ((timestamp = FileUtils.getTimestampPortionEpoch(filePath)) != null) {
				return Instant.ofEpochMilli(timestamp);
			}
		} catch (Exception e) {
			Logger.log("No timestamp found in " + filePath, e, Level.TRACE);
		}
		try (InputStream stream = Files.newInputStream(filePath)) {
			Metadata metadata = ImageMetadataReader.readMetadata(stream);

			Collection<ExifIFD0Directory> directoriesIfd0 = metadata.getDirectoriesOfType(ExifIFD0Directory.class);
			Collection<ExifSubIFDDirectory> directories = metadata.getDirectoriesOfType(ExifSubIFDDirectory.class);

			for (ExifIFD0Directory directory : directoriesIfd0) {
				Date date = directory.getDate(ExifIFD0Directory.TAG_DATETIME_ORIGINAL);
				if (date != null) {
					return date.toInstant();
				}
			}
			
			for (ExifSubIFDDirectory directory : directories) {
				Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
				if (date != null) {
					return date.toInstant();
				}
			}


			for (ExifIFD0Directory directory : directoriesIfd0) {
				Date date = directory.getDate(ExifIFD0Directory.TAG_DATETIME);
				if (date != null) {
					return date.toInstant();
				}
			}
			
			for (ExifSubIFDDirectory directory : directories) {
				Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME);
				if (date != null) {
					return date.toInstant();
				}
			}
		} catch (ImageProcessingException | IOException e) {
			Logger.log("Error during parsing of image file " + filePath
					+ " for exif data for a creation date, falling back to file creation date", e, Level.DEBUG);
		}

		// try parsing from file name		
		Matcher datePatternFullDateTimeMillisecondsMatcher = DATE_FULL_MS.matcher(filePath.getFileName().toString());
		if (datePatternFullDateTimeMillisecondsMatcher.matches()) {
			return FileUtils.getInstant(datePatternFullDateTimeMillisecondsMatcher.group(1) + datePatternFullDateTimeMillisecondsMatcher.group(2) + datePatternFullDateTimeMillisecondsMatcher.group(3));
		}

		Matcher datePatternFullDateTimeMatcher = DATE_FULL.matcher(filePath.getFileName().toString());
		if (datePatternFullDateTimeMatcher.matches()) {
			return FileUtils.getInstant(datePatternFullDateTimeMatcher.group(1) + datePatternFullDateTimeMatcher.group(2) + "000");
		}

		try {
			Logger.log("No date found in image file " + filePath + " exif data, falling back to file creation date",
					Level.INFO);
			return Files.readAttributes(filePath, BasicFileAttributes.class).lastModifiedTime().toInstant();
		} catch (IOException e) {
			throw new IllegalStateException("Could not get the creation date from file " + filePath, e);
		}
	}

	public static Duration getCreationDuration(Path filePath) {
		try (InputStream stream = Files.newInputStream(filePath)) {
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
			Logger.log("Could not parse image file exif data for a shutter duration, falling back to duration 0", e,
					Level.DEBUG);
		}

		return Duration.ZERO;
	}

	/**
	 * @param path  the {@link Path} to be deleted
	 * @param force set to true, if read only files should be deleted
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
			Files.list(source).forEach(current -> {
				try {
					Files.createDirectories(destination);
					copyRecursive(current, destination.resolve(current.getFileName()));
				} catch (IOException e) {
					throw new IllegalStateException(
							"Could not copy recursively from " + current + " to " + destination);
				}
			});
		} else {
			Path fileDest = destination;
			if (Files.isDirectory(fileDest)) {
				fileDest = destination.resolve(source.getFileName());
			}
			Files.copy(source, fileDest, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public static long getEpoch(String timestamp) {
		return getInstant(timestamp).toEpochMilli();
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

	public static Instant getInstant(String timestamp) {
		DateTimeFormatter f = new DateTimeFormatterBuilder().parseCaseInsensitive().appendValue(YEAR, 4)
				.appendValue(MONTH_OF_YEAR, 2).appendValue(DAY_OF_MONTH, 2).appendValue(ChronoField.HOUR_OF_DAY, 2)
				.appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendValue(ChronoField.SECOND_OF_MINUTE, 2)
				.appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter().withZone(ZoneId.of("Z"));
		// f = DateTimeFormatter.ofPattern("uuuuMMddHHmmssSSS");
		TemporalAccessor parsed = f.parse(timestamp);
		return Instant.from(parsed);
	}

	/**
	 * Returns the timestamp string for a given file according to
	 * {@link #getCreationDate(Path)}.
	 * 
	 * @param file
	 * @return the timestamp
	 */
	public static String getTimestamp(Path file) {
		return getTimestamp(getCreationDate(file));
	}

	/**
	 * Moves a symlink to a new folder while retaining the same target. If the
	 * symlink was absolute it will be relativized.
	 * 
	 * @param current
	 * @param destination
	 * @return
	 * @throws IOException
	 */
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

	public static long getTimestampPortionEpoch(Path current) {
		String filename = current.getFileName().toString();
		return FileUtils.getEpoch(getTimestampPortion(filename));
	}

	public static String getTimestampPortion(String filename) {
		if (filename.contains("_")) {
			return filename.substring(0, filename.indexOf('_'));	
		}
		return null;
	}

	public static String getTimestampPortion(Path path) {
		return getTimestampPortion(path.getFileName().toString());
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

	public static String getSuffix(Path path) {
		return getSuffix(path.getFileName().toString());
	}

	public static String getSuffix(String filename) {
		if (filename.contains(".")) {
			return filename.substring(filename.indexOf("."), filename.length());
		}
		return "";
	}

	public static Path removeSuffix(Path fileName) {
		return Paths.get(removeSuffix(fileName.toString()));
	}

	public static String removeSuffix(String fileName) {
		if (!fileName.contains(".")) {
			return fileName;
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
				Files.list(path).forEach(current -> {
					setReadOnlyRecursive(current);
				});
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

	public static String buildFileName(String timestamp, String name, String suffix) {
		return timestamp + "_" + name + suffix;
	}

	public static void removeEmptyFolders(Path sortingFolder) throws IOException {
		Path[] toBeDeleted = Files.list(sortingFolder).filter(current -> {
			try {
				return Files.isDirectory(current) && Files.list(current).count() == 0;
			} catch (IOException e) {
				Logger.log("Failure during listing of contents for " + current, Level.ERROR);
				return false;
			}
		}).toArray(size -> new Path[size]);

		for (Path path : toBeDeleted) {
			FileUtils.deleteRecursive(path, true);
		}
	}

	public static String getChecksum(Collection<Path> paths) {
		if (paths.size() == 0) {
			throw new IllegalArgumentException("No input files for checksum creation");
		}
		CRC32 crc = new CRC32();

		paths.stream().sorted().forEachOrdered(path -> {
			try (InputStream in = Files.newInputStream(path)) {
				final byte[] buf = new byte[4096];
				int bytesRead = in.read(buf, 0, buf.length);
				do {
					bytesRead = in.read(buf, 0, buf.length);
					crc.update(buf);
				} while (bytesRead > -1);
			} catch (IOException e) {
				Logger.log("Failure during creation of group checksum for " + path, e);
			}
		});
		return Long.toString(crc.getValue());
	}

	public static void hardlinkOrCopy(Path source, Path destination) throws IOException {
		if (!Files.isDirectory(source) && Files.isDirectory(destination)) {
			destination = destination.resolve(source.getFileName());
		}
		try {
			Files.createLink(destination, source);
		} catch (UnsupportedOperationException | FileSystemException e) {
			Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public static void moveRecursive(Path source, Path destination) throws IOException {
		if (Files.isDirectory(source)) {
			Files.list(source).forEach(current -> {
				try {
					if (Files.isDirectory(current)) {
						Path newDir = destination.resolve(current.getFileName());
						Files.createDirectories(newDir);
						moveRecursive(current, newDir);
					} else {
						moveRecursive(current, destination);
					}
				} catch (IOException e) {
					throw new IllegalStateException(
							"Could not move recursively from " + current + " to " + destination, e);
				}
			});
		} else {
			Path fileDest = destination;
			if (Files.isDirectory(fileDest)) {
				fileDest = destination.resolve(source.getFileName());
			}
			if (Files.isSymbolicLink(source)) {
				FileUtils.moveSymlink(source, destination);
			} else {
				FileUtils.hardlinkOrCopy(source, fileDest);
				Files.delete(source);
			}
		}
	}

	public static void deleteEverythingBut(Path destination_direct, Set<Path> keep) throws IOException {
		if (!Files.isDirectory(destination_direct)) {
			throw new IllegalArgumentException(destination_direct.toString() + " is not a directory");
		}
		Set<Path> absolutePaths = new HashSet<>();
		absolutePaths = Files.list(destination_direct).map(path -> path.toAbsolutePath().normalize())
				.collect(Collectors.toSet());
		for (Path p : keep) {
			absolutePaths.remove(p.toAbsolutePath().normalize());
		}
		Set<Path> cleanedPaths = new HashSet<>(absolutePaths);

		try {
			Files.list(destination_direct).forEach(current -> {
				current = current.toAbsolutePath().normalize();
				if (Files.isDirectory(current)) {
					try {
						deleteEverythingBut(current, keep);
					} catch (IOException e) {
						Logger.log("Failure during deletion of everything but " + current.toString(), e, Level.WARNING);
					}
				}
				if (cleanedPaths.contains(current)) {
					try {
						if ((Files.isDirectory(current) && Files.list(current).count() == 0)
								|| !Files.isDirectory(current)) {
							Files.deleteIfExists(current);
						}
					} catch (IOException e) {
						Logger.log("Failure during deletion of " + current.toString(), e, Level.WARNING);
					}
				}
			});
		} catch (IOException e) {
			Logger.log("Failure during preparation of cleaning " + destination_direct.toString(), e, Level.WARNING);
		}
	}

	public static void changeLinkTargetFilename(Path link, String newFileName) throws IOException {
		if (!Files.isSymbolicLink(link)) {
			throw new IllegalArgumentException("The given path is not a symlink " + link);
		}
		Path currentTarget = Files.readSymbolicLink(link);
		Path newTarget = currentTarget.getParent().resolve(newFileName);

		Logger.log("Change link target filename of " + link + " -> " + currentTarget + " to " + newTarget, Level.TRACE);

		Files.delete(link);
		Files.createSymbolicLink(link, newTarget);
	}

	public static void renameFile(Path file, Path root, String newName) throws IOException {
		Path realfile = file.toAbsolutePath();
		Files.walk(root).filter(p -> {
			if (Files.isSymbolicLink(p)) {
				try {
					Path linkTarget = FileUtils.resolve(p).toAbsolutePath();
					return realfile.equals(linkTarget);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return false;
		}).forEach(p -> {
			try {
				FileUtils.changeLinkTargetFilename(p, newName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		Path newPath = file.getParent().resolve(newName);
		Files.move(file, newPath);
		Logger.log("Renamed " + file + " to " + newPath, Level.TRACE);
		
	}

	public static Path updateFilenameToLinkTargetname(Path link) throws IOException {
		if (!Files.isSymbolicLink(link)) {
			throw new IllegalArgumentException("Argument must be a symbolic link");
		}
		return Files.move(link, link.getParent().resolve(Files.readSymbolicLink(link).getFileName()));
	}

	public static Collection<Path> getByRegex(Path root, String selectingRegex) throws IOException {
		Pattern pattern = Pattern.compile(selectingRegex);
		Predicate<String> pred = pattern.asPredicate();
		return Files.list(root).filter(p -> {
			return pred.test(p.getFileName().toString());
		}).collect(Collectors.toSet());
	}

	public static Path resolve(Path symlink) throws IOException {
		return symlink.getParent().resolve(Files.readSymbolicLink(symlink)).normalize();
	}

	public static void changeTimestamp(Path file, Path root, String targetFileStamp) throws IOException {
		String newName = FileUtils.buildFileName(targetFileStamp, FileUtils.getNamePortion(file),
				FileUtils.getSuffix(file));
		FileUtils.renameFile(file, root, newName);
	}

	public static void deleteAllFilesNotExistingIn(Path referenceDir, Path pathToBeCleaned, boolean ignoreSuffix)
			throws IOException {
		Set<Path> sourcefiles = Files.list(referenceDir).map(p -> {
			if (ignoreSuffix) {
				return FileUtils.removeSuffix(p.getFileName());
			} else {
				return p.getFileName();
			}
		}).collect(Collectors.toSet());
		Set<Path> targetfiles = Files.list(pathToBeCleaned).map(p -> {
			if (ignoreSuffix) {
				return FileUtils.removeSuffix(p.getFileName());
			} else {
				return p.getFileName();
			}
		}).collect(Collectors.toSet());
		targetfiles.removeAll(sourcefiles);
		targetfiles.forEach(p -> {
			try {
				Files.list(pathToBeCleaned).filter(c -> {
					return FileUtils.removeSuffix(c.getFileName()).equals(p);
				}).forEach(c -> {
					try {
						Files.delete(c);
					} catch (IOException e) {
						Logger.log("Delete failed", e);
					}
				});
			} catch (IOException e) {
				Logger.log("Delete failed", e);
			}
		});
	}
}
