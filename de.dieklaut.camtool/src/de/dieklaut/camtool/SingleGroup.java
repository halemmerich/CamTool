package de.dieklaut.camtool;

import java.nio.file.Path;
import java.util.Collection;

/**
 * This stores a single logical artifact, e.g. a single raw image file or
 * potentially multiple files that essentially belong together. This could be a
 * raw file with its accompanying out-of-camera JPG and rawtherapee picture
 * profile.
 * 
 * @author mboonk
 *
 */
public class SingleGroup extends AbstractGroup {

	public SingleGroup(Collection<Path> elements) {
		super(elements);
	}

	@Override
	public Result render() {
		return null;
	}

	@Override
	public boolean isMarkedAsDeleted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString() {
		return "SingleGroup: " + getAllFiles();
	}

	@Override
	public String getType() {
		return "single";
	}
}
