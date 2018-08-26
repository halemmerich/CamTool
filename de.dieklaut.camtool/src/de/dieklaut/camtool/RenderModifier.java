package de.dieklaut.camtool;

import java.nio.file.Path;

import de.dieklaut.camtool.renderjob.RenderJob;

public interface RenderModifier {

	RenderJob getRenderJob();
	
	void move(Path destination);

	Path getContainingFolder();

}