package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.util.FileUtils;

/**
 * This {@link RenderJob} renders all {@link Group}s from the given collection.
 * The given render script will be executed and can be used to post process the
 * rendered artifacts.
 * 
 * @author mboonk
 *
 */
public class RenderScriptMultiRenderJob extends RenderJob {

	private Collection<Group> groups;
	private Path renderscriptFile;

	public RenderScriptMultiRenderJob(Path renderscriptFile, Collection<Group> groups) {
		this.groups = groups;
		this.renderscriptFile = renderscriptFile;
	}

	@Override
	void storeImpl(Path destination) throws IOException {
		Path workDir = Files.createTempDirectory("camtool_workdir");

		for (Group group : groups) {
			group.getRenderJob().store(workDir);
		}

		Path resultDir = Files.createTempDirectory("camtool_results");

		if (!JavaScriptExecutor.execRenderScript(renderscriptFile, resultDir, workDir, Collections.emptyMap())) {
			throw new IllegalStateException("Render script execution failed");
		}

		FileUtils.copyRecursive(resultDir, destination);
		FileUtils.deleteRecursive(workDir, true);
		FileUtils.deleteRecursive(resultDir, true);
	}

}
