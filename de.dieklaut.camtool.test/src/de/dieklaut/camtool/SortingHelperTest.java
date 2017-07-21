package de.dieklaut.camtool;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

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
		assertEquals(3, sorting.size());
		
	}

	@Test
	public void testConstructorSingleGroupSubdir() throws IOException {
		Path subdir = Files.createDirectory(getTestFolder().resolve("subdir"));
		Path file4arw = Files.createFile(subdir.resolve("file4.ARW"));
		Path file4jpg = Files.createFile(subdir.resolve("file4.JPG"));

		Collection<Group> sorting = SortingHelper.identifyGroups(getTestFolder());
		assertEquals(1, sorting.size());
		Group group = sorting.iterator().next();
		Collection<Path> files = group.getAllFiles();

		assertEquals(2, files.size());
		assertTrue(files.contains(file4arw));
		assertTrue(files.contains(file4jpg));
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

		assertEquals(4, files.size());
		assertTrue(files.contains(collection));
		assertTrue(files.contains(file2arw));
		assertTrue(files.contains(file3arw));
		assertTrue(files.contains(file4arw));
	}
}
