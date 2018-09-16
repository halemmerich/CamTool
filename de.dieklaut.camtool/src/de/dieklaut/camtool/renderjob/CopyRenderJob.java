package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

public class CopyRenderJob extends RenderJob {

	private Collection<Path> source;
	
	public CopyRenderJob(Path ... source) {
		this.source = Arrays.asList(source);
	}

	@Override
	void storeImpl(Path destination) throws IOException {
		for (Path current : source) {
			Path destinationFile = destination;
			if (Files.isDirectory(destination)) {
				destinationFile = destination.resolve(current.getFileName());
			}
			
			Files.copy(current.toRealPath(), destinationFile);
		}
	}

}
