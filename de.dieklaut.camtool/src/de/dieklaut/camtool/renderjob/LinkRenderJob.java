package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class LinkRenderJob extends RenderJob {

	private Path source;

	public LinkRenderJob(Path source) {
		this.source = source;
	}

	@Override
	public Set<Path> storeImpl(Path destination) throws IOException {
		Set<Path> rendered = new HashSet<>();
		if (Files.isDirectory(destination)) {
			destination = destination.resolve(source.getFileName());
		}
		Files.createSymbolicLink(destination, source);
		rendered.add(destination);
		return rendered;
	}

	@Override
	public Set<Path> getPredictedResultsImpl(Path destination) throws IOException {
		if (Files.isDirectory(destination)) {
			destination = destination.resolve(source.getFileName());
		}
		Set<Path> result = new HashSet<>();
		result.add(destination);
		return result;
	}
}
