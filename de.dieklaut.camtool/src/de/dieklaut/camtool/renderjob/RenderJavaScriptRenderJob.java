package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.operations.RenderFilter;
import de.dieklaut.camtool.util.FileUtils;

public class RenderJavaScriptRenderJob extends RenderJob {

	private Path mainFile;
	private String name;
	private Path[] helperFiles;

	public RenderJavaScriptRenderJob(String name, Path mainFile, Path... helperFiles) {
		this.mainFile = mainFile;
		this.name = name;
		this.helperFiles = helperFiles;
	}

	@Override
	public Set<Path> storeImpl(Path destination, Collection<RenderFilter> renderFilters) throws IOException {
		Map<String, Object> map = new HashMap<>();

		map.put("helperFiles", helperFiles);

		Path tempDir = Files.createTempDirectory(Constants.TEMP_FOLDER_PREFIX);

		boolean result = JavaScriptExecutor.execRenderScript(mainFile, name, destination, tempDir, map);

		FileUtils.deleteRecursive(tempDir, true);

		if (!result) {
			throw new IllegalStateException("Storing of " + mainFile + " failed");
		}

		// FIXME: correctly handle recursive files/folders
		try (var l = Files.list(destination)){
			return new HashSet<>(Files.list(destination).collect(Collectors.toSet()));
		}
	}

	@Override
	public Set<Path> getPredictedResultsImpl(Path destination, Collection<RenderFilter> renderFilters) throws IOException {
		return null;
	}

}
