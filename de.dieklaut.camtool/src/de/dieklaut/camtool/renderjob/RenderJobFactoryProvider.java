package de.dieklaut.camtool.renderjob;

import java.nio.file.Path;

public interface RenderJobFactoryProvider {

	public RenderJob forFile(Path mainFile, Path ... helperFiles);

}
