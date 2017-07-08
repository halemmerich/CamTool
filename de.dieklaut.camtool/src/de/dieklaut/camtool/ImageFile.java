package de.dieklaut.camtool;

import java.io.IOException;
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

public class ImageFile implements SourceFile {

	private Path filePath;

	public ImageFile(Path file) {
		if(!file.toFile().exists()) {
			throw new IllegalArgumentException("The given file " + file + "does not exist");
		}
		this.filePath = file;
	}
	
	@Override
	public Instant getCreationDate() {
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
			Logger.log("Could not parse image file for a creation date, falling back to file creation date", e);
		}
		
		try {
			return Files.readAttributes(filePath, BasicFileAttributes.class).creationTime().toInstant();
		} catch (IOException e) {
			throw new IllegalStateException("Could not get a creation date for the file", e);
		}
	}
}
