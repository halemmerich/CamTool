package de.dieklaut.camtool;

import static org.junit.Assert.assertEquals;

import java.time.Instant;

import org.junit.Test;

public class GroupTimestampComparatorTest {
	@Test
	public void testCompare() {
		GroupTimestampComparator comp = new GroupTimestampComparator();

		Group earlier = new SingleGroup(null) {
			@Override
			public Instant getTimestamp() {
				return Instant.ofEpochSecond(5);
			}
		};

		Group same = new SingleGroup(null) {
			@Override
			public Instant getTimestamp() {
				return Instant.ofEpochSecond(5);
			}
		};

		Group later = new SingleGroup(null) {
			@Override
			public Instant getTimestamp() {
				return Instant.ofEpochSecond(6);
			}
		};

		assertEquals(-1, comp.compare(earlier, later));
		assertEquals(0, comp.compare(earlier, same));
		assertEquals(1, comp.compare(later, earlier));
	}
}
