package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.operations.RenderFilter;

public class MultiRenderJob extends RenderJob {

	private Collection<Group> groups;

	public MultiRenderJob(Collection<Group> groups) {
		this.groups = groups;
	}

	@Override
	public Set<Path> storeImpl(Path destination, Collection<RenderFilter> renderFilters) throws IOException {
		Set<Path> rendered = new HashSet<>();
		for (Group g : groups) {
			rendered.addAll(g.getRenderJob(renderFilters).store(destination, renderFilters));
		}
		return rendered;
	}

	@Override
	public Set<Path> getPredictedResultsImpl(Path destination, Collection<RenderFilter> renderFilters) throws IOException {
		Set<Path> result = new HashSet<>();
		for (Group g : groups) {
			result.addAll(g.getRenderJob(renderFilters).getPredictedResults(destination, renderFilters));
		}
		return result;
	}

}
