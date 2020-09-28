package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import de.dieklaut.camtool.operations.RenderFilter;

public class NullRenderJob extends RenderJob {

	@Override
	public Set<Path> storeImpl(Path destination, Collection<RenderFilter> renderFilters) throws IOException {
		return Collections.emptySet();
	}

	@Override
	public Set<Path> getPredictedResultsImpl(Path destination, Collection<RenderFilter> renderFilters) throws IOException {
		return Collections.emptySet();
	}

}
