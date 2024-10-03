package de.dieklaut.camtool;

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

import de.dieklaut.camtool.Logger.Level;

public class SortingHelper {

	public static void combineSeries(Collection<Group> sorting, int detectSeriesTimeDiff, int minimumNumberOfFiles) {
		List<Group> sortedByTimestampGroups = new ArrayList<>(sorting);

		sortedByTimestampGroups.sort(new GroupTimestampComparator());
		//TODO: Split into separate series for different cameras
		
		Map<String, List<Group>> groupsByCreator = new HashMap<>();
		
		for (Group current : sortedByTimestampGroups) {
			String creator = current.getCreator();
			if (!groupsByCreator.containsKey(creator)) {
				groupsByCreator.put(creator, new LinkedList<>());
			}
			Logger.log(current.getName() + " has creator " + creator, Level.TRACE);
			groupsByCreator.get(creator).add(current);
		}

		for (List<Group> current : groupsByCreator.values()) {
			combine(sorting, detectSeriesTimeDiff, minimumNumberOfFiles, current);
		}
	}

	private static void combine(Collection<Group> sorting, int detectSeriesTimeDiff, int minimumNumberOfFiles,
			List<Group> sortedByTimestampGroups) {
		Instant lastTimestamp = null;
		Duration lastDuration = null;

		List<Group> currentSeries = new LinkedList<>();

		Collection<MultiGroup> foundSeriesGroups = new HashSet<>();

		Collection<Group> groupsToBeRemoved = new HashSet<>();
		
		for (Group currentGroup : sortedByTimestampGroups) {
			Logger.log(currentGroup.getName() + " - " + currentGroup.getTimestamp() + " " + currentGroup.getDuration(), Level.TRACE);
			
			if (lastTimestamp != null && !lastTimestamp.plusSeconds(detectSeriesTimeDiff).plus(lastDuration).isAfter(currentGroup.getTimestamp())) {
				finishCurrentSeries(currentSeries, foundSeriesGroups, minimumNumberOfFiles, groupsToBeRemoved);
				lastTimestamp = null;
				lastDuration = null;
			}
			
			if (lastTimestamp == null
					|| lastTimestamp.plusSeconds(detectSeriesTimeDiff).plus(lastDuration).isAfter(currentGroup.getTimestamp())) {
				currentSeries.add(currentGroup);
				lastTimestamp = currentGroup.getTimestamp();
				lastDuration = currentGroup.getDuration();
			} else {
				finishCurrentSeries(currentSeries, foundSeriesGroups, minimumNumberOfFiles, groupsToBeRemoved);
				lastTimestamp = null;
				lastDuration = null;
			}
		}
		if (!currentSeries.isEmpty()) {
			finishCurrentSeries(currentSeries, foundSeriesGroups, minimumNumberOfFiles, groupsToBeRemoved);
		}

		sorting.removeAll(groupsToBeRemoved);

		sorting.addAll(foundSeriesGroups);
	}

	private static void finishCurrentSeries(List<Group> currentSeries, Collection<MultiGroup> foundSeriesGroups, int minimumNumberOfFiles,
			Collection<Group> groupsToBeRemoved) {
		if (currentSeries.size() >= minimumNumberOfFiles) {
			Collection<Group> seriesGroupContents = new HashSet<>();
			for (Group toBeMoved : currentSeries) {
				groupsToBeRemoved.add(toBeMoved);
				seriesGroupContents.add(toBeMoved);
			}
			
			foundSeriesGroups.add(new MultiGroup(seriesGroupContents));
		}
		
		currentSeries.clear();
	}
	
	/**
	 * Serch groups recursively to find one by name.
	 * 
	 * TODO: Remove this method and implement a unique way to identify groups without danger of duplicates.
	 * 
	 * @param groups
	 * @param nameOfGroup
	 * @return the found group or null if none are found
	 */
	public static Group findGroupByName(Collection<Group> groups, String nameOfGroup) {
		Group candidate = null;
		for (Group group : groups) {
			if (nameOfGroup.equals(group.getName())) {
				if (candidate == null) {
					candidate = group;
				} else {
					throw new IllegalStateException("The given name " + nameOfGroup + " is not unique");
				}
			}
			if (group instanceof MultiGroup) {
				Group result = findGroupByName(((MultiGroup) group).getGroups(), nameOfGroup);
				if (result != null) {
					if (candidate == null) {
						candidate = result;
					} else {
						throw new IllegalStateException("The given name " + nameOfGroup + " is not unique");
					}
				}
			}
		}
		return candidate;
	}

	public static Group findGroupByPath(Collection<Group> groups, Path groupPath) {
		for (Group group : groups) {
			if (group instanceof MultiGroup) {
				if (group.getContainingFolder().toAbsolutePath().equals(groupPath.toAbsolutePath())) {
					return group;
				}
				
				Group result = findGroupByPath(((MultiGroup) group).getGroups(), groupPath);
				if (result != null) {
					return result;
				}
			}
			if (group.getAllFiles().stream().anyMatch(path -> path.toAbsolutePath().equals(groupPath.toAbsolutePath()))) {
				return group;
			}
		}
		return null;
	}

	public static String detectSortingFromDir(Path workingDir) {
		if (Files.exists(workingDir.resolve(Constants.SORTED_FILE_NAME))) {
			return workingDir.getFileName().toString();
		} else {
			if (workingDir.getParent() != null) {
				return detectSortingFromDir(workingDir.getParent());
			} else {
				throw new IllegalArgumentException("No sorting detecting in " + workingDir + " or parents");
			}
		}
	}
}
