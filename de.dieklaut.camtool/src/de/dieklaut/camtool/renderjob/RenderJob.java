package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;

/**
 * This is a result of a {@link Group#getRenderJob()} operation. It can manage
 * the created artifacts.
 * 
 * @author mboonk
 *
 */
public abstract class RenderJob {

	public Set<Path> store(Path destination) throws IOException {
		Logger.log("Performing store for " + this.getClass().getSimpleName(), Level.INFO);
		return storeImpl(destination);
	}

	/**
	 * Performs the rendering and stores the result at the given destination.
	 * 
	 * @param destination
	 * @return
	 * @throws IOException
	 */
	abstract public Set<Path> storeImpl(Path destination) throws IOException;

	public Collection<? extends Path> getPredictedResults(Path destination) throws IOException {
		Logger.log("Performing prediction for " + this.getClass().getSimpleName(), Level.INFO);
		return getPredictedResultsImpl(destination);
	}

	abstract public Set<Path> getPredictedResultsImpl(Path destination) throws IOException;
}
