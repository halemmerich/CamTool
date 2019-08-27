package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.dieklaut.camtool.util.FileUtils;

public class CopyRenderJob extends RenderJob {

	private Collection<Path> source;

	public CopyRenderJob(Path... source) {
		this.source = Arrays.asList(source);
	}

	@Override
	public Set<Path> storeImpl(Path destination) throws IOException {
		Set<Path> rendered = new HashSet<>();
		for (Path current : source) {
			Path destinationFile = destination;
			if (Files.isDirectory(destination)) {
				destinationFile = destination.resolve(current.getFileName());
			}

			FileUtils.hardlinkOrCopy(current.toRealPath(), destinationFile);
			rendered.add(destinationFile);
		}
		return rendered;
	}

	@Override
	public Set<Path> getPredictedResultsImpl(Path destination) throws IOException {
		Set<Path> rendered = new HashSet<>();
		for (Path current : source) {
			Path destinationFile = destination;
			if (Files.isDirectory(destination)) {
				destinationFile = destination.resolve(current.getFileName());
			}

			FileUtils.hardlinkOrCopy(current.toRealPath(), destinationFile);
			rendered.add(destinationFile);
		}
		return rendered;
	}

}
