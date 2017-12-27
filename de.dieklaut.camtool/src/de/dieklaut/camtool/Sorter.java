package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public interface Sorter {

	Collection<Group> identifyGroups(Path path) throws IOException;

}
