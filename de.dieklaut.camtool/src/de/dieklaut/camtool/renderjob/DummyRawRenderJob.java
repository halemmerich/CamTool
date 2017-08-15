package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.dieklaut.camtool.util.FileUtils;

public class DummyRawRenderJob implements RenderJob {

	private Path element;

	public DummyRawRenderJob(Path element) {
		this.element = element;
	}

	@Override
	public void store(Path destination) {
		try {
			if (Files.isDirectory(destination)) {
				Files.createFile(destination.resolve(FileUtils.removeSuffix(element.getFileName().toString()) + ".jpg"));
			} else {
				Files.createFile(destination);
			}
		} catch (IOException e) {
			throw new IllegalStateException("Creation fake jpg result file failed", e);
		}
	}

}
