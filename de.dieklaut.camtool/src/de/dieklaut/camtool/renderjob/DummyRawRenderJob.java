package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import de.dieklaut.camtool.util.FileUtils;

/**
 * This "renders" a file by creating an empty file with .jpg suffix.
 * 
 * @author mboonk
 *
 */
public class DummyRawRenderJob extends RenderJob {

	private Path element;

	public DummyRawRenderJob(Path element) {
		this.element = element;
	}

	@Override
	public Set<Path> storeImpl(Path destination) {
		Set<Path> rendered = new HashSet<>();
		try {
			if (Files.isDirectory(destination)) {
				destination = destination.resolve(FileUtils.removeSuffix(element.getFileName().toString()) + ".jpg");
			}
			Files.deleteIfExists(destination);
			Files.createFile(destination);
			rendered.add(destination);
		} catch (IOException e) {
			throw new IllegalStateException("Creation fake jpg result file failed", e);
		}
		return rendered;
	}

	@Override
	public Set<Path> getPredictedResultsImpl(Path destination) throws IOException {
		if (Files.isDirectory(destination)) {
			destination = destination.resolve(FileUtils.removeSuffix(element.getFileName().toString()) + ".jpg");
		}
		Set<Path> result = new HashSet<>();
		result.add(destination);
		return result;
	}

}
