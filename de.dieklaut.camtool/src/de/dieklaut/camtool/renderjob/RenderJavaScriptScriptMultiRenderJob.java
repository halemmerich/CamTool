package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.MultiGroup;
import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.operations.RenderFilter;
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
	public
	Set<Path> storeImpl(Path destination, Collection<RenderFilter> renderFilters) throws IOException {
		Path workDir = Files.createTempDirectory("camtool_workdir");

		for (Group group : multiGroup.getGroups()) {
			group.getRenderJob(renderFilters).store(workDir, renderFilters);
		}

		Path resultDir = Files.createTempDirectory("camtool_results");
		
		if (JavaScriptExecutor.execRenderScript(renderscriptFile, multiGroup.getName(), resultDir, workDir, Collections.emptyMap())) {
			FileUtils.copyRecursive(resultDir, destination);
		} else {
			Logger.log("Render script execution failed", Level.ERROR);
		}
		
		try (var l = Files.list(resultDir)){
			Set<Path> rendered = l.collect(Collectors.toSet());
			//FIXME: correctly handle recursive files/folders
			FileUtils.deleteRecursive(workDir, true);
			FileUtils.deleteRecursive(resultDir, true);
			return rendered;
		}
	}

	@Override
	public
	Set<Path> getPredictedResultsImpl(Path destination, Collection<RenderFilter> renderFilters) throws IOException {
		return null;
	}

}
