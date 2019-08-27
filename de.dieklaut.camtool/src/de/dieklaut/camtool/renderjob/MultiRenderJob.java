package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.dieklaut.camtool.Group;

public class MultiRenderJob extends RenderJob {

	private Collection<Group> groups;

	public MultiRenderJob(Collection<Group> groups) {
		this.groups = groups;
	}

	@Override
	public Set<Path> storeImpl(Path destination) throws IOException {
		Set<Path> rendered = new HashSet<>();
		for (Group g : groups) {
			rendered.addAll(g.getRenderJob().store(destination));
		}
		return rendered;
	}

	@Override
	public Set<Path> getPredictedResultsImpl(Path destination) throws IOException {
		Set<Path> result = new HashSet<>();
		for (Group g : groups) {
			result.addAll(g.getRenderJob().getPredictedResults(destination));
		}
		return result;
	}

}
