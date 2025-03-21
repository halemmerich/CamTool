package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.operations.RenderFilter;

/**
 * This is a result of a {@link Group#getRenderJob()} operation. It can manage
 * the created artifacts.
 * 
 * @author mboonk
 *
 */
public abstract class RenderJob {

	public Set<Path> store(Path destination, Collection<RenderFilter> renderFilters) throws IOException {
		Logger.log("Performing store for " + this.getClass().getSimpleName(), Level.INFO);
		return storeImpl(destination, renderFilters);
	}

	/**
	 * Performs the rendering and stores the result at the given destination.
	 * 
	 * @param destination
	 * @return
	 * @throws IOException
	 */
	abstract public Set<Path> storeImpl(Path destination, Collection<RenderFilter> renderFilters) throws IOException;

	public Collection<Path> getPredictedResults(Path destination, Collection<RenderFilter> renderFilters) throws IOException {
		Logger.log("Performing prediction for " + this.getClass().getSimpleName(), Level.INFO);
		return getPredictedResultsImpl(destination, renderFilters);
	}

	abstract public Set<Path> getPredictedResultsImpl(Path destination, Collection<RenderFilter> renderFilters) throws IOException;
}
