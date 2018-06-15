package de.dieklaut.camtool.renderjob;

import java.nio.file.Path;

import de.dieklaut.camtool.FileTypeHelper;
import de.dieklaut.camtool.external.RawTherapeeWrapper;

public class RenderJobFactory {
	/**
	 * Create RawTherapee jobs for raw files.
	 */
	public static boolean useRawtherapee = true;
	
	/**
	 * Create dummy jobs for raw files
	 */
	public static boolean useDummyRawJob = true;

	public static RenderJob getRenderJob(Path mainFile, Path[] helperFiles, String name) {
		if (FileTypeHelper.isRenderscript(mainFile)) {
			return new RenderScriptRenderJob(name, mainFile, helperFiles);
		} else if (FileTypeHelper.isRawImageFile(mainFile)) {
			if (useRawtherapee) {
				return new RawTherapeeRenderJob(new RawTherapeeWrapper(), mainFile, helperFiles);
			} else if (useDummyRawJob) {
				return new DummyRawRenderJob(mainFile);
			}
		} else if (FileTypeHelper.isVideoFile(mainFile)) {
			return new LinkRenderJob(mainFile);
		}
		
		return new CopyRenderJob(mainFile);
	}

}