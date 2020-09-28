package de.dieklaut.camtool.operations;

import java.nio.file.Path;
import java.util.Collection;

public interface RenderFilter {
	public boolean isFiltered(Path primaryFile, Collection<Path> collection);

	public String getShortString();
}
