package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

/**
 * This identifies files belonging to the same logical group for later processing.
 * @author mboonk
 *
 */
public interface Sorter {

	Collection<Group> identifyGroups(Path path) throws IOException;

}
