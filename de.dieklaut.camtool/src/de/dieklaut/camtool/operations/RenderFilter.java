package de.dieklaut.camtool.operations;

import java.nio.file.Path;
import java.util.Collection;

public interface RenderFilter {
	/**
	 * @param primaryFile
	 * @param collection
	 * @return true, iff this should be ignored
	 */
	public boolean isFiltered(Path primaryFile, Collection<Path> collection);

	public String getShortString();
}
