package de.dieklaut.camtool;

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

	public static void combineSeries(Collection<Group> sorting, int detectSeriesTimeDiff) {
		List<Group> sortedByTimestampGroups = new ArrayList<>(sorting);

		sortedByTimestampGroups.sort(new GroupTimestampComparator());
		//TODO: Split into separate series for different cameras
		
		Map<String, List<Group>> groupsByCreator = new HashMap<>();
		
		for (Group current : sortedByTimestampGroups) {
			String creator = current.getCreator();
			if (!groupsByCreator.containsKey(creator)) {
				groupsByCreator.put(creator, new LinkedList<>());
			}
			groupsByCreator.get(creator).add(current);
		}

		for (List<Group> current : groupsByCreator.values()) {
			combine(sorting, detectSeriesTimeDiff, current);
		}
	}

	private static void combine(Collection<Group> sorting, int detectSeriesTimeDiff,
			List<Group> sortedByTimestampGroups) {
		Instant lastTimestamp = null;
		Duration lastDuration = null;

		List<Group> currentSeries = new LinkedList<>();

		Collection<MultiGroup> foundSeriesGroups = new HashSet<>();

		Collection<Group> groupsToBeRemoved = new HashSet<>();
		
		for (Group currentGroup : sortedByTimestampGroups) {
			Logger.log(currentGroup.getName() + " - " + currentGroup.getTimestamp() + " " + currentGroup.getDuration(), Level.TRACE);
			
			if (lastTimestamp != null && !lastTimestamp.plusSeconds(detectSeriesTimeDiff).plus(lastDuration).isAfter(currentGroup.getTimestamp())) {
				finishCurrentSeries(currentSeries, foundSeriesGroups, groupsToBeRemoved);
				lastTimestamp = null;
				lastDuration = null;
			}
			
			if (lastTimestamp == null
					|| lastTimestamp.plusSeconds(detectSeriesTimeDiff).plus(lastDuration).isAfter(currentGroup.getTimestamp())) {
				currentSeries.add(currentGroup);
				lastTimestamp = currentGroup.getTimestamp();
				lastDuration = currentGroup.getDuration();
			} else {
				finishCurrentSeries(currentSeries, foundSeriesGroups, groupsToBeRemoved);
				lastTimestamp = null;
				lastDuration = null;
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
		if (currentSeries.size() > 1) {
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
		for (Group group : groups) {
			if (nameOfGroup.equals(group.getName())) {
				return group;
			}
			if (group instanceof MultiGroup) {
				Group result = findGroupByName(((MultiGroup) group).getGroups(), nameOfGroup);
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}
}
