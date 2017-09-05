package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Path;

import de.dieklaut.camtool.FileTypeHelper;
import de.dieklaut.camtool.RawTherapeeWrapper;

public class RawTherapeeRenderJob implements RenderJob {

	private Path[] helperFiles;
	private Path mainFile;
	private RawTherapeeWrapper processWrapper;

	public RawTherapeeRenderJob(RawTherapeeWrapper processWrapper, Path mainFile, Path ... helperFiles) {
		this.mainFile = mainFile;
		this.helperFiles = helperFiles;
		this.processWrapper = processWrapper;
	}

	@Override
	public void store(Path destination) throws IOException {
		processWrapper.setInputFile(mainFile.toAbsolutePath().toString());
		processWrapper.setOutputFile(destination.toAbsolutePath().toString());
		processWrapper.setJpgQuality(95, 3);
		
		for (Path currentFile : helperFiles) {
			if (FileTypeHelper.isRawTherapeeProfile(currentFile)) {
				processWrapper.addProfileOption(currentFile.toAbsolutePath().toString());
			}
		}
		
		processWrapper.process();

	}

}
