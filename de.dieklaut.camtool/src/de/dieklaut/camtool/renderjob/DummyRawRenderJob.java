package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.dieklaut.camtool.util.FileUtils;

/**
 * This "renders" a file by creating an empty file with .jpg suffix.
 * @author mboonk
 *
 */
public class DummyRawRenderJob extends RenderJob {

	private Path element;

	public DummyRawRenderJob(Path element) {
		this.element = element;
	}

	@Override
	void storeImpl(Path destination) {
		try {
			if (Files.isDirectory(destination)) {
				destination = destination.resolve(FileUtils.removeSuffix(element.getFileName().toString()) + ".jpg");
			}
			Files.deleteIfExists(destination);
			Files.createFile(destination);
		} catch (IOException e) {
			throw new IllegalStateException("Creation fake jpg result file failed", e);
		}
	}

}
