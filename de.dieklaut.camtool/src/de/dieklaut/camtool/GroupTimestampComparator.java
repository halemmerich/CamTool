package de.dieklaut.camtool;

import java.time.Instant;
import java.util.Comparator;

final class GroupTimestampComparator implements Comparator<Group> {
	@Override
	public int compare(Group o1, Group o2) {
		Instant timestamp1 = o1.getTimestamp();
		Instant timestamp2 = o2.getTimestamp();
		
		if (timestamp1.equals(timestamp2)) {
			//identical timestamp, sort by name
			Comparator<String> stringComp = Comparator.naturalOrder();
			return stringComp.compare(o1.getName(), o2.getName());
		}
		
		return timestamp1.compareTo(timestamp2);
	}
}