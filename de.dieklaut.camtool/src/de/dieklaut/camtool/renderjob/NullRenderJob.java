package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;

public class NullRenderJob extends RenderJob {

	@Override
	public Set<Path> storeImpl(Path destination) throws IOException {
		return Collections.emptySet();
	}

	@Override
	public Set<Path> getPredictedResultsImpl(Path destination) throws IOException {
		return Collections.emptySet();
	}

}
