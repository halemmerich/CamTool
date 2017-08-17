package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Path;

import de.dieklaut.camtool.ExternalTool;
import de.dieklaut.camtool.FileTypeHelper;
import de.dieklaut.camtool.RawTherapeeWrapper;

public class RawTherapeeRenderJob implements RenderJob {

	private Path[] helperFiles;
	private Path mainFile;
	private ExternalTool processWrapper;

	public RawTherapeeRenderJob(ExternalTool processWrapper, Path mainFile, Path ... helperFiles) {
		this.mainFile = mainFile;
		this.helperFiles = helperFiles;
		this.processWrapper = processWrapper;
	}

	@Override
	public void store(Path destination) throws IOException {
		RawTherapeeWrapper command = new RawTherapeeWrapper();
		command.setInputFile(mainFile.toAbsolutePath().toString());
		command.setOutputFile(destination.toAbsolutePath().toString());
		command.setJpgQuality(95, 3);
		
		for (Path currentFile : helperFiles) {
			if (FileTypeHelper.isRawTherapeeProfile(currentFile)) {
				command.addProfileOption(currentFile.toAbsolutePath().toString());
			}
		}
		
		processWrapper.process();

	}

}
