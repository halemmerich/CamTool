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

	public static Collection<Group> identifyGroups(Path path) throws IOException {

		Collection<Group> groups = new HashSet<>();
		Collection<Path> collectionFiles = new HashSet<>();

		Map<String, Set<Path>> groupNamesToPaths = new HashMap<>();

		Files.list(path).forEach(currentPath -> {
			if (!Files.isDirectory(currentPath)) {
				String currentFileName = currentPath.getFileName().toString();

				if (currentFileName.equals(Constants.SORTED_FILE_NAME)) {
					return;
				}

				if (currentFileName.endsWith(Constants.FILE_NAME_COLLECTION_SUFFIX)
						|| currentFileName.endsWith(Constants.FILE_NAME_SERIES_SUFFIX)) {
					collectionFiles.add(currentPath);
				} else {

					String currentGroupName = currentFileName;
					if (currentFileName.contains(".")) {
						currentGroupName = currentFileName.substring(0, currentFileName.indexOf("."));
					}

					if (!groupNamesToPaths.containsKey(currentGroupName)) {
						groupNamesToPaths.put(currentGroupName, new HashSet<>());
					}
					groupNamesToPaths.get(currentGroupName).add(currentPath);
				}
			}
		});

		Map<String, Group> groupNamesToGroup = new HashMap<>();

		for (String currentGroupName : groupNamesToPaths.keySet()) {
			Set<Path> currentGroupPaths = groupNamesToPaths.get(currentGroupName);

			SingleGroup newGroup = new SingleGroup(currentGroupPaths);
			groups.add(newGroup);
			groupNamesToGroup.put(currentGroupName, newGroup);
		}

		for (Path collectionFile : collectionFiles) {
			Set<Group> collectionGroups = new HashSet<>();
			for (String currentFileFromCollection : Files.readAllLines(collectionFile)) {
				Group groupForCollection = groupNamesToGroup.get(FileUtils.getGroupName(currentFileFromCollection));
				collectionGroups.add(groupForCollection);
				groups.remove(groupForCollection);
			}
			Group newGroup = null;
			if (collectionFile.endsWith(Constants.FILE_NAME_SERIES_SUFFIX)) {
				newGroup = new SeriesGroup(collectionGroups, collectionFile);
			} else {
				newGroup = new MultiGroup(collectionGroups, collectionFile);
			}
			groups.add(newGroup);
		}

		return groups;
	}

	public static void combineSeries(Collection<Group> sorting) {
		List<Group> sortedByTimestampGroups = new ArrayList<>(sorting);

		sortedByTimestampGroups.sort(new GroupTimestampComparator());

		Instant lastTimestamp = null;
		Duration lastDuration = null;

		List<Group> currentSeries = new LinkedList<>();

		Collection<SeriesGroup> foundSeriesGroups = new HashSet<>();
		
		Collection<Group> groupsToBeRemoved = new HashSet<>();
		
		for (Group currentGroup : sortedByTimestampGroups) {
			System.out.println(currentGroup.getName() + " " + currentGroup.getTimestamp() + "  " + currentGroup.getDuration());
			if (lastTimestamp == null || lastTimestamp.plusSeconds(2).plus(lastDuration)
					.isAfter(currentGroup.getTimestamp())) {
				currentSeries.add(currentGroup);
				lastTimestamp = currentGroup.getTimestamp();
				lastDuration = currentGroup.getDuration();
			} else {
				finishCurrentSeries(currentSeries, foundSeriesGroups, groupsToBeRemoved);
			}
		}

		finishCurrentSeries(currentSeries, foundSeriesGroups, groupsToBeRemoved);
		
		sorting.removeAll(groupsToBeRemoved);
		
		sorting.addAll(foundSeriesGroups);
	}

	private static void finishCurrentSeries(List<Group> currentSeries, Collection<SeriesGroup> foundSeriesGroups,
			Collection<Group> groupsToBeRemoved) {
		Collection<Group> seriesGroupContents = new HashSet<>();
		for (Group toBeMoved : currentSeries) {
			groupsToBeRemoved.add(toBeMoved);
			seriesGroupContents.add(toBeMoved);
		}
		foundSeriesGroups.add(new SeriesGroup(seriesGroupContents));
	}

}
