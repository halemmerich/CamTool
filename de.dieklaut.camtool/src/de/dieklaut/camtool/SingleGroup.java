package de.dieklaut.camtool;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This stores a single logical artifact, e.g. a single raw image file or
 * potentially multiple files that essentially belong together. This could be a
 * raw file with its accompanying out-of-camera JPG and rawtherapee picture
 * profile.
 * 
 * @author mboonk
 *
 */
public class SingleGroup implements Group {

	private Set<Path> files;

	public SingleGroup(Set<Path> containedFiles) {
		files = new HashSet<>();
		files.addAll(containedFiles);
	}

	@Override
	public Collection<Path> getAllFiles() {
		return new HashSet<Path>(files);
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
	public void moveToFolder(Path destination) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		return "SingleGroup: " + getAllFiles();
	}
}
