package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Path;

import de.dieklaut.camtool.Group;

/**
 * This is a result of a {@link Group#getRenderJob()} operation. It can manage the created artifacts.
 * @author mboonk
 *
 */
public interface RenderJob {
	public void store(Path destination) throws IOException;
}
