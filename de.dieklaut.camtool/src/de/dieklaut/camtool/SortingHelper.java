package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SortingHelper {

	public static Collection<Group> identifyGroups(Path path) throws IOException {

		Collection<Group> groups = new HashSet<>();
		Collection<Path> pathsUsed = new HashSet<>();

		Set<Path> collections = new HashSet<>();

		Files.list(path).forEach(currentPath -> {
			if (Files.isDirectory(currentPath)) {
				Collection<Group> identifiedGroups;
				try {
					identifiedGroups = identifyGroups(currentPath);
				} catch (IOException e) {
					throw new IllegalStateException("Could not load group from " + path, e);
				}
				for (Group g : identifiedGroups) {
					pathsUsed.addAll(g.getAllFiles());
				}
				groups.addAll(identifiedGroups);
			} else {
				String currentFileName = currentPath.getFileName().toString();
				if (currentFileName.equals(Constants.SORTED_FILE_NAME)) {
					return;
				}
				if (currentFileName.endsWith(".camtool_collection")) {
					collections.add(currentPath);
				}
			}
		});

		for (Path current : collections) {
			Set<Path> paths = new HashSet<>();
			paths.add(current);
			for (String currentFileName : Files.readAllLines(current)) {
				paths.add(current.getParent().resolve(currentFileName));
			}
			MultiGroup newGroup = new MultiGroup(paths);
			groups.add(newGroup);
			pathsUsed.addAll(newGroup.getAllFiles());
		}

		Map<String, Set<Path>> groupNamesToPaths = new HashMap<>();

		Files.list(path).forEach(currentPath -> {
			if (Files.isDirectory(currentPath)) {
				return;
			}
			if (Collections.frequency(pathsUsed, currentPath) > 0) {
				return;
			}

			String currentFileName = currentPath.getFileName().toString();
			String currentGroupName = currentFileName;
			if (currentFileName.contains(".")) {
				currentGroupName = currentFileName.substring(0, currentFileName.indexOf("."));
			}
			if (!groupNamesToPaths.containsKey(currentGroupName)) {
				groupNamesToPaths.put(currentGroupName, new HashSet<>());
			}
			groupNamesToPaths.get(currentGroupName).add(currentPath);
		});

		for (String currentGroupName : groupNamesToPaths.keySet()) {
			groups.add(new SingleGroup(groupNamesToPaths.get(currentGroupName)));
		}

		return groups;
	}

}
