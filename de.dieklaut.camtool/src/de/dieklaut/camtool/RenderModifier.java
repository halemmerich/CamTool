package de.dieklaut.camtool;

import java.nio.file.Path;
import java.util.Collection;

import de.dieklaut.camtool.operations.RenderFilter;
import de.dieklaut.camtool.renderjob.RenderJob;

public interface RenderModifier {

	RenderJob getRenderJob(Collection<RenderFilter> renderFilters);
	
	void move(Path destination);

	Path getContainingFolder();

	Collection<Path> getAllFiles();

}
