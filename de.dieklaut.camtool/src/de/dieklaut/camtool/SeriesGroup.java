package de.dieklaut.camtool;

import java.nio.file.Path;
import java.util.Collection;

public class SeriesGroup extends MultiGroup {

	public SeriesGroup(Collection<Group> groups) {
		super(groups);
	}

	public SeriesGroup(Collection<Group> groups, Path markerFile) {
		super(groups, markerFile);
	}

}
