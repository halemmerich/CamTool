package de.dieklaut.camtool.renderjob;

import java.nio.file.Path;

import de.dieklaut.camtool.FileTypeHelper;
import de.dieklaut.camtool.RawTherapeeWrapper;

public class RenderJobFactory {
	
	private boolean useRawtherapee = false;
	private boolean useLinkRenderer = true;
	private static RenderJobFactoryProvider instance;
	
	static {
		instance = new DefaultRenderJobFactoryProvider();
	}
	
	private RenderJobFactory() {
		
	}
	
	public static RenderJobFactoryProvider getInstance(){
		return instance;
	}
	
	public static void replaceFactoryInstance(RenderJobFactoryProvider replacement){
		instance = replacement;
	}
	
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
