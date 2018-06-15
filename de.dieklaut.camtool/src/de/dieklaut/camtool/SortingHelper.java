package de.dieklaut.camtool;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.dieklaut.camtool.Logger.Level;

public class SortingHelper {

	public static void combineSeries(Collection<Group> sorting) {
		List<Group> sortedByTimestampGroups = new ArrayList<>(sorting);

		sortedByTimestampGroups.sort(new GroupTimestampComparator());

		Instant lastTimestamp = null;
		Duration lastDuration = null;

		List<Group> currentSeries = new LinkedList<>();

		Collection<MultiGroup> foundSeriesGroups = new HashSet<>();

		Collection<Group> groupsToBeRemoved = new HashSet<>();

		int timeDiff = 2;
		
		for (Group currentGroup : sortedByTimestampGroups) {
			Logger.log(currentGroup.getName() + " - " + currentGroup.getTimestamp() + " " + currentGroup.getDuration(), Level.TRACE);
			
			if (lastTimestamp != null && !lastTimestamp.plusSeconds(timeDiff).plus(lastDuration).isAfter(currentGroup.getTimestamp())) {
				finishCurrentSeries(currentSeries, foundSeriesGroups, groupsToBeRemoved);
				lastTimestamp = null;
				lastDuration = null;
			}
			
			if (lastTimestamp == null
					|| lastTimestamp.plusSeconds(timeDiff).plus(lastDuration).isAfter(currentGroup.getTimestamp())) {
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

}
