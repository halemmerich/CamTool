package de.dieklaut.camtool.renderjob;

import java.nio.file.Path;

import de.dieklaut.camtool.FileTypeHelper;

public class DefaultRenderJobFactoryProvider implements RenderJobFactoryProvider {
	
	private boolean useRawtherapee = false;
	private boolean useLinkRenderer = true;
	
	@Override
	public RenderJob forFile(Path mainFile, Path ... helperFiles) {
		if (FileTypeHelper.isRawImageFile(mainFile)) {
			if (useRawtherapee) {
				return new RawtherapeeRenderJob(mainFile, helperFiles);
			} else if (useLinkRenderer) {
				return new LinkRenderJob(mainFile);
			}
		} else if (FileTypeHelper.isVideoFile(mainFile)) {
			return new LinkRenderJob(mainFile);
		}
		
		throw new IllegalStateException("Could not determine a renderer to be used");
	}
}
