package de.dieklaut.camtool;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

public class GroupTimestampComparatorTest {
	@Test
	public void testCompare() {
		GroupTimestampComparator comp = new GroupTimestampComparator();

		Collection<Path> elements = Arrays.asList(Paths.get("test.asdf"));
		Path root = Paths.get("/testpath");
		
		Group earlier = new SingleGroup(elements, root) {
			@Override
			public Instant getTimestamp() {
				return Instant.ofEpochSecond(5);
			}
		};

		Group same = new SingleGroup(elements, root) {
			@Override
			public Instant getTimestamp() {
				return Instant.ofEpochSecond(5);
			}
		};

		Group later = new SingleGroup(elements, root) {
			@Override
			public Instant getTimestamp() {
				return Instant.ofEpochSecond(6);
			}
		};

		assertEquals(-1, comp.compare(earlier, later));
		assertEquals(0, comp.compare(earlier, same));
		assertEquals(1, comp.compare(later, earlier));
	}

	@Test
	public void testCompareIdenticalStamp() {
		GroupTimestampComparator comp = new GroupTimestampComparator();

		Collection<Path> elements1 = Arrays.asList(Paths.get("test1.asdf"));
		Collection<Path> elements2 = Arrays.asList(Paths.get("test2.asdf"));
		
		Path root = Paths.get("/testpath");
		
		Group earlier = new SingleGroup(elements1, root) {
			@Override
			public Instant getTimestamp() {
				return Instant.ofEpochSecond(5);
			}
		};

		Group later = new SingleGroup(elements2, root) {
			@Override
			public Instant getTimestamp() {
				return Instant.ofEpochSecond(5);
			}
		};

		assertEquals(-1, comp.compare(earlier, later));
		assertEquals(1, comp.compare(later, earlier));
	}
}
