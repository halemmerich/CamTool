package de.dieklaut.camtool;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;

/**
 * This stores multiple files belonging together but essentially not the same.
 * This could be the source files for a panorama or a HDR-Stack. These files
 * must normally somehow processed to be useful.
 * 
 * @author mboonk
 *
 */
public class MultiGroup implements Group {

	private Collection<Path> paths;

	public MultiGroup(Collection<Path> paths) {
		this.paths = paths;
	}

	@Override
	public Collection<Path> getAllFiles() {
		return new HashSet<>(paths);
	}

	@Override
	public Result render() {
		// TODO Auto-generated method stub
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
		return "MultiGroup:\n" + getAllFiles();
	}
}
