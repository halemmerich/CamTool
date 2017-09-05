package de.dieklaut.camtool.renderjob;

import java.nio.file.Path;

import de.dieklaut.camtool.FileTypeHelper;
import de.dieklaut.camtool.RawTherapeeWrapper;

public class DefaultRenderJobFactoryProvider implements RenderJobFactoryProvider {
	
	private boolean useRawtherapee = false;
	private boolean useLinkRenderer = true;
	
	public void setUseRawtherapee(boolean useRawtherapee) {
		this.useRawtherapee = useRawtherapee;
	}

	public void setUseLinkRenderer(boolean useLinkRenderer) {
		this.useLinkRenderer = useLinkRenderer;
	}
	
	@Override
	public RenderJob forFile(Path mainFile, Path ... helperFiles) {
		if (FileTypeHelper.isRawImageFile(mainFile)) {
			if (useRawtherapee) {
				return new RawTherapeeRenderJob(new RawTherapeeWrapper(), mainFile, helperFiles);
			} else if (useLinkRenderer) {
				return new LinkRenderJob(mainFile);
			}
		} else if (FileTypeHelper.isVideoFile(mainFile)) {
			return new LinkRenderJob(mainFile);
		}
		
		throw new IllegalStateException("Could not determine a renderer to be used");
	}
}
