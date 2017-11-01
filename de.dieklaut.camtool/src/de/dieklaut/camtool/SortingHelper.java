package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dieklaut.camtool.util.FileUtils;

public class SortingHelper {

	public static boolean useRawTherapee = false;

	public static Collection<Group> identifyGroups(Path path) throws IOException {

		Collection<Group> groups = new HashSet<>();
		Collection<Path> camtoolFiles = new HashSet<>();

		Map<String, Set<Path>> groupNamesToPaths = new HashMap<>();

		Files.list(path).forEach(currentPath -> {
			if (!Files.isDirectory(currentPath)) {
				String currentFileName = currentPath.getFileName().toString();

				if (currentFileName.equals(Constants.SORTED_FILE_NAME)) {
					return;
				}

				if (currentFileName.matches(".*\\" + Constants.FILE_NAME_CAMTOOL_SUFFIX + "[^\\.]*$")) {
					camtoolFiles.add(currentPath);
				} else {

					String currentGroupName = FileUtils.getGroupName(currentFileName);

					if (!groupNamesToPaths.containsKey(currentGroupName)) {
						groupNamesToPaths.put(currentGroupName, new HashSet<>());
					}
					groupNamesToPaths.get(currentGroupName).add(currentPath);
				}
			}
		});

		Map<String, Group> groupNamesToGroup = new HashMap<>();

		createSingleGroups(groups, groupNamesToPaths, groupNamesToGroup);

		createCollections(groups, camtoolFiles, groupNamesToGroup);

		return groups;
	}

	private static void createSingleGroups(Collection<Group> groups, Map<String, Set<Path>> groupNamesToPaths,
			Map<String, Group> groupNamesToGroup) {
		for (String currentGroupName : groupNamesToPaths.keySet()) {
			Set<Path> currentGroupPaths = groupNamesToPaths.get(currentGroupName);

			SingleGroup newGroup = new SingleGroup(currentGroupPaths);
			groups.add(newGroup);
			groupNamesToGroup.put(currentGroupName, newGroup);
		}
	}

	/**
	 * Creates collections of existing groups using the
	 * 
	 * @param groups
	 * @param camtoolFiles
	 * @param groupNamesToGroup
	 * @throws IOException
	 */
	private static void createCollections(Collection<Group> groups, Collection<Path> camtoolFiles,
			Map<String, Group> groupNamesToGroup) throws IOException {
		for (Path camtoolFile : camtoolFiles) {
			if (camtoolFile.getFileName().toString().endsWith(Constants.FILE_NAME_COLLECTION_SUFFIX)) {
				Set<Group> collectionGroups = new HashSet<>();
				for (String currentFileFromCollection : Files.readAllLines(camtoolFile)) {
					Group groupForCollection = groupNamesToGroup.get(FileUtils.getGroupName(currentFileFromCollection));
					collectionGroups.add(groupForCollection);
					groups.remove(groupForCollection);
				}
				Group newGroup = new MultiGroup(collectionGroups, camtoolFile);
				groups.add(newGroup);
				groupNamesToGroup.put(newGroup.getName(), newGroup);
			}
		}
		for (Path camtoolFile : camtoolFiles) {
			if (camtoolFile.getFileName().endsWith(Constants.FILE_NAME_RENDERSCRIPT_SUFFIX)) {
					
				Group group = groupNamesToGroup.get(FileUtils.getGroupName(camtoolFile));
					
				if (group != null && group instanceof MultiGroup) {
					((MultiGroup)group).setRenderscriptFile(camtoolFile);
				}
			}
		}
	}

	public static void combineSeries(Collection<Group> sorting) {
		List<Group> sortedByTimestampGroups = new ArrayList<>(sorting);

		sortedByTimestampGroups.sort(new GroupTimestampComparator());

		Instant lastTimestamp = null;
		Duration lastDuration = null;

		List<Group> currentSeries = new LinkedList<>();

		Collection<MultiGroup> foundSeriesGroups = new HashSet<>();

		Collection<Group> groupsToBeRemoved = new HashSet<>();

		for (Group currentGroup : sortedByTimestampGroups) {
			System.out.println(
					currentGroup.getName() + " " + currentGroup.getTimestamp() + "  " + currentGroup.getDuration());
			if (lastTimestamp == null
					|| lastTimestamp.plusSeconds(2).plus(lastDuration).isAfter(currentGroup.getTimestamp())) {
				currentSeries.add(currentGroup);
				lastTimestamp = currentGroup.getTimestamp();
				lastDuration = currentGroup.getDuration();
			} else {
				finishCurrentSeries(currentSeries, foundSeriesGroups, groupsToBeRemoved);
			}
		}
		if (!currentSeries.isEmpty()) {
			finishCurrentSeries(currentSeries, foundSeriesGroups, groupsToBeRemoved);
		}

		sorting.removeAll(groupsToBeRemoved);

		sorting.addAll(foundSeriesGroups);
	}

	private static void finishCurrentSeries(List<Group> currentSeries, Collection<MultiGroup> foundSeriesGroups,
			Collection<Group> groupsToBeRemoved) {
		Collection<Group> seriesGroupContents = new HashSet<>();
		for (Group toBeMoved : currentSeries) {
			groupsToBeRemoved.add(toBeMoved);
			seriesGroupContents.add(toBeMoved);
		}
		foundSeriesGroups.add(new MultiGroup(seriesGroupContents));
	}

}
