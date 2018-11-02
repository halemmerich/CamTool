package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.MultiGroup;
import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.util.FileUtils;

/**
 * This {@link RenderJob} renders all {@link Group}s from the given collection.
 * The given render script will be executed and can be used to post process the
 * rendered artifacts.
 * 
 * @author mboonk
 *
 */
public class RenderJavaScriptScriptMultiRenderJob extends RenderJob {

	private MultiGroup multiGroup;
	private Path renderscriptFile;

	public RenderJavaScriptScriptMultiRenderJob(Path renderscriptFile, MultiGroup multiGroup) {
		this.multiGroup = multiGroup;
		this.renderscriptFile = renderscriptFile;
	}

	@Override
	void storeImpl(Path destination) throws IOException {
		Path workDir = Files.createTempDirectory("camtool_workdir");

		for (Group group : multiGroup.getGroups()) {
			group.getRenderJob().store(workDir);
		}

		Path resultDir = Files.createTempDirectory("camtool_results");
		
		if (JavaScriptExecutor.execRenderScript(renderscriptFile, multiGroup.getName(), resultDir, workDir, Collections.emptyMap())) {
			FileUtils.copyRecursive(resultDir, destination);
		} else {
			Logger.log("Render script execution failed", Level.ERROR);
		}

		FileUtils.deleteRecursive(workDir, true);
		FileUtils.deleteRecursive(resultDir, true);
	}

}
