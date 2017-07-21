package de.dieklaut.camtool;

import java.nio.file.Path;
import java.util.Collection;

/**
 * This stores multiple files belonging together but essentially not the same.
 * This could be the source files for a panorama or a HDR-Stack. These files
 * must normally somehow processed to be useful.
 * 
 * @author mboonk
 *
 */
public class MultiGroup extends AbstractGroup {

	public MultiGroup(Collection<Path> elements) {
		super(elements);
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
	public String toString() {
		return "MultiGroup:\n" + getAllFiles();
	}

	@Override
	public String getType() {
		return "multi";
	}
}
