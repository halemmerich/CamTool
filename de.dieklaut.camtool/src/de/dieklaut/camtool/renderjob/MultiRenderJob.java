package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import de.dieklaut.camtool.Group;

public class MultiRenderJob implements RenderJob {

	private Collection<Group> groups;

	public MultiRenderJob(Collection<Group> groups) {
		this.groups = groups;
	}

	@Override
	public void store(Path destination) throws IOException {
		for (Group g : groups) {
			g.getRenderJob().store(destination);
		}
	}

}
