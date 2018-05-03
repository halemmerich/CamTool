package de.dieklaut.camtool;

import java.nio.file.Path;
import java.time.Instant;

import de.dieklaut.camtool.util.FileUtils;

public class ImageFile implements SourceFile {

	private Path filePath;
	Instant creationDate;

	public ImageFile(Path file) {
		if (!file.toFile().exists()) {
			throw new IllegalArgumentException("The given file " + file + "does not exist");
		}
		this.filePath = file;
	}

	@Override
	public Instant getCreationDate() {
		if (creationDate == null) {
			creationDate = FileUtils.getCreationDate(filePath);
		}
		return creationDate;
	}
}
