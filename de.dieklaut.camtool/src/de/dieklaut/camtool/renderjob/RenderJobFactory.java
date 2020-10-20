package de.dieklaut.camtool.renderjob;

import java.nio.file.Path;

import de.dieklaut.camtool.FileTypeHelper;
import de.dieklaut.camtool.external.RawTherapeeWrapper;
import de.dieklaut.camtool.external.PdfRenderJob;

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
			return new RenderJavaScriptRenderJob(name, mainFile, helperFiles);
		} else if (FileTypeHelper.isRawImageFile(mainFile) || hasPp3(helperFiles, mainFile) ) {
			if (useRawtherapee) {
				return new RawTherapeeRenderJob(new RawTherapeeWrapper(), mainFile, helperFiles);
			} else if (useDummyRawJob) {
				return new DummyRawRenderJob(mainFile);
			}
		} else if (FileTypeHelper.isPdfFile(mainFile)) {
			return new PdfRenderJob(mainFile);
		}
		return new CopyRenderJob(mainFile);
	}

	private static boolean hasPp3(Path[] helperFiles, Path mainFile) {
		for (Path current : helperFiles) {
			if (mainFile != null && current.getFileName().toString().equals(mainFile.getFileName().toString() + ".pp3")) {
				return true;
			} else if (current.getFileName().toString().endsWith(".pp3")) {
				return true;
			}
		}
		return false;
	}

}
