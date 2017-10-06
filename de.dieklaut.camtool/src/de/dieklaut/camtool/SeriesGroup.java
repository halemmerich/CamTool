package de.dieklaut.camtool;

import java.nio.file.Path;
import java.util.Collection;

import de.dieklaut.camtool.renderjob.RenderJob;

public class SeriesGroup extends MultiGroup {

	public SeriesGroup(Collection<Group> groups) {
		super(groups);
	}

	public SeriesGroup(Collection<Group> groups, Path markerFile) {
		super(groups, markerFile);
	}

	@Override
	public RenderJob getRenderJob() {
		// TODO Auto-generated method stub
		return null;
	}

}
