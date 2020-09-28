package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.dieklaut.camtool.FileTypeHelper;
import de.dieklaut.camtool.external.RawTherapeeWrapper;
import de.dieklaut.camtool.external.RawTherapeeWrapper.FileType;
import de.dieklaut.camtool.operations.RenderFilter;
import de.dieklaut.camtool.util.FileUtils;

public class RawTherapeeRenderJob extends RenderJob {

	private Path[] helperFiles;
	private Path mainFile;
	private RawTherapeeWrapper processWrapper;
	private static final FileType DEFAULT_FILE_TYPE = FileType.PNG_8;

	public RawTherapeeRenderJob(RawTherapeeWrapper processWrapper, Path mainFile, Path ... helperFiles) {
		this.mainFile = mainFile;
		this.helperFiles = helperFiles;
		this.processWrapper = processWrapper;
	}

	@Override
	public
	Set<Path> storeImpl(Path destination, Collection<RenderFilter> renderFilters) throws IOException {
		if (Files.isDirectory(destination)) {
			destination = destination.resolve(FileUtils.removeSuffix(mainFile.getFileName().toString()) + "." + DEFAULT_FILE_TYPE.getSuffix());
		}
		
		processWrapper.setInputFile(mainFile.toAbsolutePath().toString());
		processWrapper.setOutputFile(destination.toAbsolutePath().toString());
		
		for (Path currentFile : helperFiles) {
			if (FileTypeHelper.isRawTherapeeProfile(currentFile)) {
				processWrapper.addProfileOption(currentFile.toAbsolutePath().toString());
			}
		}
		
		processWrapper.process();

		Set<Path> rendered = new HashSet<>();
		rendered.add(destination);
		return rendered;
	}

	@Override
	public
	Set<Path> getPredictedResultsImpl(Path destination, Collection<RenderFilter> renderFilters) throws IOException {
		if (Files.isDirectory(destination)) {
			destination = destination.resolve(FileUtils.removeSuffix(mainFile.getFileName().toString()) + "." + DEFAULT_FILE_TYPE.getSuffix());
		}
		
		Set<Path> result = new HashSet<>();
		result.add(destination);
		return result;
	}

}
