package de.dieklaut.camtool.renderjob;

import java.nio.file.Path;

import de.dieklaut.camtool.FileTypeHelper;
import de.dieklaut.camtool.RawTherapeeWrapper;

public class DefaultRenderJobFactoryProvider implements RenderJobFactoryProvider {
	
	private boolean useRawtherapee = false;
	
	public void setUseRawtherapee(boolean useRawtherapee) {
		this.useRawtherapee = useRawtherapee;
	}
	
	@Override
	public RenderJob forFile(Path mainFile, Path ... helperFiles) {
		if (FileTypeHelper.isRawImageFile(mainFile)) {
			if (useRawtherapee) {
				return new RawTherapeeRenderJob(new RawTherapeeWrapper(), mainFile, helperFiles);
			}
		} else if (FileTypeHelper.isVideoFile(mainFile)) {
			return new LinkRenderJob(mainFile);
		}
		
		return new CopyRenderJob(mainFile);
	}
}
