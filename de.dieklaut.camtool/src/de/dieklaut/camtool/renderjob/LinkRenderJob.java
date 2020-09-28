package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.dieklaut.camtool.operations.RenderFilter;

public class LinkRenderJob extends RenderJob {

	private Path source;

	public LinkRenderJob(Path source) {
		this.source = source;
	}

	@Override
	public Set<Path> storeImpl(Path destination, Collection<RenderFilter> renderFilters) throws IOException {
		Set<Path> rendered = new HashSet<>();
		if (Files.isDirectory(destination)) {
			destination = destination.resolve(source.getFileName());
		}
		Files.createSymbolicLink(destination, source);
		rendered.add(destination);
		return rendered;
	}

	@Override
	public Set<Path> getPredictedResultsImpl(Path destination, Collection<RenderFilter> renderFilters) throws IOException {
		if (Files.isDirectory(destination)) {
			destination = destination.resolve(source.getFileName());
		}
		Set<Path> result = new HashSet<>();
		result.add(destination);
		return result;
	}
}
