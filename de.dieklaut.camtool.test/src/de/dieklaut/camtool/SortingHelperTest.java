package de.dieklaut.camtool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hamcrest.core.IsInstanceOf;
import org.junit.Test;

public class SortingHelperTest extends FileBasedTest {
	
	@Test
	public void testConstructorComplexStructure() throws IOException {
		Files.createFile(getTestFolder().resolve("file1.ARW"));
		Files.createFile(getTestFolder().resolve("file1.JPG"));

		Path collection = Files.createFile(getTestFolder().resolve("file.camtool_collection"));
		Files.write(collection, "file2.ARW\nfile3.ARW\nfile4.ARW\n".getBytes());
		Files.createFile(getTestFolder().resolve("file2.ARW"));
		Files.createFile(getTestFolder().resolve("file3.ARW"));
		Files.createFile(getTestFolder().resolve("file4.ARW"));

		Path subdir = Files.createDirectory(getTestFolder().resolve("subdir"));
		Files.createFile(subdir.resolve("file4.ARW"));
		Files.createFile(subdir.resolve("file4.JPG"));
		
		Collection<Group> sorting = SortingHelper.identifyGroups(getTestFolder());
		assertEquals(2, sorting.size());
		
	}

	@Test
	public void testConstructorSingleGroup() throws IOException {
		Path file1arw = Files.createFile(getTestFolder().resolve("file1.ARW"));
		Path file1jpg = Files.createFile(getTestFolder().resolve("file1.JPG"));

		Collection<Group> sorting = SortingHelper.identifyGroups(getTestFolder());
		assertEquals(1, sorting.size());
		Group group = sorting.iterator().next();
		Collection<Path> files = group.getAllFiles();

		assertEquals(2, files.size());
		assertTrue(files.contains(file1arw));
		assertTrue(files.contains(file1jpg));
	}

	@Test
	public void testConstructorSingleGroupCollection() throws IOException {
		Path collection = Files.createFile(getTestFolder().resolve("file.camtool_collection"));
		Files.write(collection, "file2.ARW\nfile3.ARW\nfile4.ARW\n".getBytes());
		Path file2arw = Files.createFile(getTestFolder().resolve("file2.ARW"));
		Path file3arw = Files.createFile(getTestFolder().resolve("file3.ARW"));
		Path file4arw = Files.createFile(getTestFolder().resolve("file4.ARW"));

		Collection<Group> sorting = SortingHelper.identifyGroups(getTestFolder());
		assertEquals(1, sorting.size());
		Group group = sorting.iterator().next();
		Collection<Path> files = group.getAllFiles();

		assertEquals(3, files.size());
		assertTrue(files.contains(file2arw));
		assertTrue(files.contains(file3arw));
		assertTrue(files.contains(file4arw));
		assertEquals(collection, ((MultiGroup)group).getMarkerFile());
	}
	
	@Test
	public void testCombineSeriesSingleFile() throws IOException {
		List<Path> arw = new LinkedList<Path>();
		
		for (int i = 6; i <= 9; i++) {
			String filename = String.format("series_%02d.ARW", i);
			String sourcepath = "series/" + filename;
			arw.add(Files.copy(TestFileHelper.getTestResource(sourcepath), getTestFolder().resolve(filename)));
		}

		Collection<Group> sorting = SortingHelper.identifyGroups(getTestFolder());
		
		SortingHelper.combineSeries(sorting);
		
		assertEquals(1, sorting.size());
		Group group = sorting.iterator().next();
		
		assertThat(group, new IsInstanceOf(MultiGroup.class));
		
		SeriesGroup seriesGroup = (SeriesGroup) group;
		assertEquals(4, seriesGroup.getAllFiles().size());
	}
}
