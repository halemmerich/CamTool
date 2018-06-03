package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.util.FileUtils;

public class RenderScriptRenderJob extends RenderJob {

	private Path mainFile;
	private String name;
	private Path[] helperFiles;

	public RenderScriptRenderJob(String name, Path mainFile, Path ... helperFiles) {
		this.mainFile = mainFile;
		this.name = name;
		this.helperFiles = helperFiles;
	}

	@Override
	void storeImpl(Path destination) throws IOException {
		Map<String, Object> map = new HashMap<>();
		
		map.put("helperFiles", helperFiles);
		
		Path tempDir = Files.createTempDirectory(Constants.TEMP_FOLDER_PREFIX);
		
		boolean result = JavaScriptExecutor.execRenderScript(mainFile, name, destination, tempDir, map);				
		
		FileUtils.deleteRecursive(tempDir, true);
		
		if (!result) {
			throw new IllegalStateException("Storing of " + mainFile + " failed");
		}
	}

}
