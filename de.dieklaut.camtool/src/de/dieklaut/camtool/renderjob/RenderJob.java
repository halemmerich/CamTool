package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Path;

import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;

/**
 * This is a result of a {@link Group#getRenderJob()} operation. It can manage the created artifacts.
 * @author mboonk
 *
 */
public abstract class RenderJob {
	
	public void store(Path destination) throws IOException {
		Logger.log("Performing store for " + this.getClass().getSimpleName(), Level.INFO);
		storeImpl(destination);
	}
	
	/**
	 * Performs the rendering and stores the result at the given destination.
	 * @param destination
	 * @throws IOException
	 */
	abstract void storeImpl(Path destination) throws IOException;
}
