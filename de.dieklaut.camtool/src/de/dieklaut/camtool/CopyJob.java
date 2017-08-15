package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.dieklaut.camtool.renderjob.RenderJob;

public class CopyJob implements RenderJob {

	private Path source;
	
	public CopyJob(Path source) {
		this.source = source;
	}

	@Override
	public void store(Path destination) throws IOException {
		
		if (Files.isDirectory(destination)) {
			destination = destination.resolve(source.getFileName());
		}
		
		Files.copy(source.toRealPath(), destination);
	}

}
