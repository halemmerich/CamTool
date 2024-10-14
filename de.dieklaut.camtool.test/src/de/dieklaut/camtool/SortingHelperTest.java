package de.dieklaut.camtool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

public class SortingHelperTest extends FileBasedTest{
	
	private static Collection<Path> copy(Path source, Path dest) throws IOException {
		return Arrays.asList(new Path [] { Files.copy(source, dest) });
	}
	
	@Test
	public void testCombineSeries() throws IOException {
		Collection<Group> groups = new LinkedList<>();
		
		Path source = TestFileHelper.getTestResource("series/series_09.ARW");
		groups.add(new SingleGroup(copy(source, getTestFolder().resolve("series_09.ARW")), getTestFolder()));
		
		source = TestFileHelper.getTestResource("series/series_07.ARW");
		groups.add(new SingleGroup(copy(source, getTestFolder().resolve("series_07.ARW")), getTestFolder()));

		source = TestFileHelper.getTestResource("series/series_06.ARW");
		groups.add(new SingleGroup(copy(source, getTestFolder().resolve("series_06.ARW")), getTestFolder()));
		
		source = TestFileHelper.getTestResource("series/series_08.ARW");
		groups.add(new SingleGroup(copy(source, getTestFolder().resolve("series_08.ARW")), getTestFolder()));
				
		SortingHelper.combineSeries(groups, 4, 2);
		
		assertEquals(1, groups.size());
		assertEquals(4, ((MultiGroup)groups.stream().findFirst().get()).getGroups().size());
		
	}
	@Test
	public void testCombineSeriesNotEnoughFiles() throws IOException {
		Collection<Group> groups = new LinkedList<>();
		
		Path source = TestFileHelper.getTestResource("series/series_09.ARW");
		groups.add(new SingleGroup(copy(source, getTestFolder().resolve("series_09.ARW")), getTestFolder()));
		
		source = TestFileHelper.getTestResource("series/series_07.ARW");
		groups.add(new SingleGroup(copy(source, getTestFolder().resolve("series_07.ARW")), getTestFolder()));

		source = TestFileHelper.getTestResource("series/series_06.ARW");
		groups.add(new SingleGroup(copy(source, getTestFolder().resolve("series_06.ARW")), getTestFolder()));
		
		source = TestFileHelper.getTestResource("series/series_08.ARW");
		groups.add(new SingleGroup(copy(source, getTestFolder().resolve("series_08.ARW")), getTestFolder()));
				
		SortingHelper.combineSeries(groups, 5, 5);
		
		assertEquals(4, groups.size());
	}
	
	@Test
	public void testCombineMultipleSeries() throws IOException {
		Collection<Group> groups = new LinkedList<>();
		
		Path source = TestFileHelper.getTestResource("series/series_09.ARW");
		groups.add(new SingleGroup(copy(source, getTestFolder().resolve("series_09.ARW")), getTestFolder()));
		
		source = TestFileHelper.getTestResource("series/series_07.ARW");
		groups.add(new SingleGroup(copy(source, getTestFolder().resolve("series_07.ARW")), getTestFolder()));

		source = TestFileHelper.getTestResource("series/series_06.ARW");
		groups.add(new SingleGroup(copy(source, getTestFolder().resolve("series_06.ARW")), getTestFolder()));
		
		source = TestFileHelper.getTestResource("series/series_08.ARW");
		groups.add(new SingleGroup(copy(source, getTestFolder().resolve("series_08.ARW")), getTestFolder()));
		
		source = TestFileHelper.getTestResource("stack/stack_01.jpg");
		groups.add(new SingleGroup(copy(source, getTestFolder().resolve("stack_01.jpg")), getTestFolder()));
		
		source = TestFileHelper.getTestResource("stack/stack_02.jpg");
		groups.add(new SingleGroup(copy(source, getTestFolder().resolve("stack_02.jpg")), getTestFolder()));
		
		source = TestFileHelper.getTestResource("stack/stack_03.jpg");
		groups.add(new SingleGroup(copy(source, getTestFolder().resolve("stack_03.jpg")), getTestFolder()));
				
		SortingHelper.combineSeries(groups, 1, 2);
		
		assertEquals(2, groups.size());
		
	}
	
	@Test
	public void testFindGroupByPath() throws IOException {
		Collection<Group> groups = new LinkedList<>();
		
		Path source = TestFileHelper.getTestResource("series/series_09.ARW");
		groups.add(new SingleGroup(copy(source, getTestFolder().resolve("series_09.ARW")), getTestFolder()));
		
		source = TestFileHelper.getTestResource("series/series_07.ARW");
		Path file = Files.copy(source, getTestFolder().resolve("series_07.ARW"));
		SingleGroup expectedGroup = getGroupWithFile(file, getTestFolder());
		groups.add(expectedGroup);
		
		Path subdir =  Files.createDirectories(getTestFolder().resolve("subdir"));
		
		source = TestFileHelper.getTestResource("series/series_06.ARW");
		Group sub1 = getGroupWithFile(Files.copy(source, subdir.resolve("series_06.ARW")), subdir);
		
		source = TestFileHelper.getTestResource("series/series_08.ARW");
		Group sub2 = getGroupWithFile(Files.copy(source, subdir.resolve("series_08.ARW")), subdir);
		
		MultiGroup multi = new MultiGroup(Arrays.asList(new Group [] {sub1, sub2}));
		groups.add(multi);

		assertSame(expectedGroup, SortingHelper.findGroupByPath(groups, file, getTestFolder()));
		assertSame(multi, SortingHelper.findGroupByPath(groups, sub1.getContainingFolder(), getTestFolder()));
		assertSame(sub1, SortingHelper.findGroupByPath(groups, sub1.getAllFiles().iterator().next(), getTestFolder()));
	}
	
	@Test
	public void testFindGroupByPathWithGroupName() throws IOException {
		Collection<Group> groups = new LinkedList<>();
		
		Path source = TestFileHelper.getTestResource("series/series_09.ARW");
		groups.add(getGroupWithFile(Files.copy(source, getTestFolder().resolve("series_09.ARW")), getTestFolder()));
		
		source = TestFileHelper.getTestResource("series/series_07.ARW");
		Path file = Files.copy(source, getTestFolder().resolve("series_07.ARW"));
		SingleGroup expectedGroup = getGroupWithFile(file, getTestFolder());
		groups.add(expectedGroup);
		
		Path subdir =  Files.createDirectories(getTestFolder().resolve("subdir").resolve("subdir2"));
		
		source = TestFileHelper.getTestResource("series/series_06.ARW");
		Group sub1 = getGroupWithFile(Files.copy(source, subdir.resolve("series_06.ARW")), subdir);
		
		source = TestFileHelper.getTestResource("series/series_08.ARW");
		Group sub2 = getGroupWithFile(Files.copy(source, subdir.resolve("series_08.ARW")), subdir);
		
		MultiGroup multi = new MultiGroup(Arrays.asList(new Group [] {sub1, sub2}));
		groups.add(multi);

		assertSame(multi, SortingHelper.findGroupByPath(groups, Paths.get("subdir").resolve("subdir2"), getTestFolder()));
	}
	
	@Test
	public void testFindGroupByName() throws IOException {
		Collection<Group> groups = new LinkedList<>();
		
		Path source = TestFileHelper.getTestResource("series/series_09.ARW");
		groups.add(getGroupWithFile(Files.copy(source, getTestFolder().resolve("series_09.ARW")), getTestFolder()));
		
		source = TestFileHelper.getTestResource("series/series_07.ARW");
		Path file = Files.copy(source, getTestFolder().resolve("series_07.ARW"));
		SingleGroup expectedGroup = getGroupWithFile(file, getTestFolder());
		groups.add(expectedGroup);
		
		Path subdir =  Files.createDirectories(getTestFolder().resolve("subdir"));
		
		source = TestFileHelper.getTestResource("series/series_06.ARW");
		Group sub1 = getGroupWithFile(Files.copy(source, subdir.resolve("series_06.ARW")), subdir);
		
		source = TestFileHelper.getTestResource("series/series_08.ARW");
		Group sub2 = getGroupWithFile(Files.copy(source, subdir.resolve("series_08.ARW")), subdir);
		
		MultiGroup multi = new MultiGroup(Arrays.asList(new Group [] {sub1, sub2}));
		groups.add(multi);

		assertSame(expectedGroup, SortingHelper.findGroupByName(groups, "series_07"));
		assertSame(multi, SortingHelper.findGroupByName(groups, "subdir"));
		assertSame(sub1, SortingHelper.findGroupByName(groups, "series_06"));
	}
	
	@Test
	public void testDetectSortingFromDir() throws IOException {
		Files.createDirectories(getTestFolder().resolve("sub").resolve("sub2"));
		Files.createFile(getTestFolder().resolve("sub").resolve(Constants.SORTED_FILE_NAME));

		assertEquals("sub", SortingHelper.detectSortingFromDir(getTestFolder().resolve("sub")));
		assertEquals("sub", SortingHelper.detectSortingFromDir(getTestFolder().resolve("sub").resolve("sub2")));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDetectSortingFromDirNoSorting() throws IOException {
		Files.createDirectories(getTestFolder().resolve("sub").resolve("sub2"));

		assertEquals("sub", SortingHelper.detectSortingFromDir(getTestFolder().resolve("sub")));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDetectSortingFromDirNoSortingSubFolder() throws IOException {
		Files.createDirectories(getTestFolder().resolve("sub").resolve("sub2"));

		assertEquals("sub", SortingHelper.detectSortingFromDir(getTestFolder().resolve("sub").resolve("sub2")));
	}
	
	@Test
	public void testPathMatchesAbsoluteSameLevelGroupName() throws IOException {
		Files.createFile(getTestFolder().resolve(".notagroup"));
		var g = getGroupWithFile(Files.copy(TestFileHelper.getTestResource("series/series_08.ARW"), getTestFolder().resolve("file.ARW")), getTestFolder());
		assertTrue(SortingHelper.pathMatches(g, getTestFolder().resolve("file"), getTestFolder()));
	}
	
	@Test
	public void testPathMatchesAbsoluteSameLevelFileName() throws IOException {
		Files.createFile(getTestFolder().resolve(".notagroup"));
		var g = getGroupWithFile(Files.copy(TestFileHelper.getTestResource("series/series_08.ARW"), getTestFolder().resolve("file.ARW")), getTestFolder());
		assertTrue(SortingHelper.pathMatches(g, getTestFolder().resolve("file.ARW"), getTestFolder()));
	}
	
	@Test
	public void testPathMatchesAbsoluteOneLevelDeepGroupName() throws IOException {
		Files.createFile(getTestFolder().resolve(".notagroup"));
		var sub = Files.createDirectories(getTestFolder().resolve("sub"));
		var g = getGroupWithFile(Files.copy(TestFileHelper.getTestResource("series/series_08.ARW"), sub.resolve("file.ARW")), sub);
		assertTrue(SortingHelper.pathMatches(g, sub, getTestFolder()));
	}
	
	@Test
	public void testPathMatchesAbsoluteOneLevelDeepFileName() throws IOException {
		Files.createFile(getTestFolder().resolve(".notagroup"));
		var sub = Files.createDirectories(getTestFolder().resolve("sub"));
		var g = getGroupWithFile(Files.copy(TestFileHelper.getTestResource("series/series_08.ARW"), sub.resolve("file.ARW")), sub);
		assertTrue(SortingHelper.pathMatches(g, sub.resolve("file.ARW"), getTestFolder()));
	}
	
	@Test
	public void testPathMatchesAbsoluteTwoLevelsDeepGroupName() throws IOException {
		Files.createFile(getTestFolder().resolve(".notagroup"));
		var sub = Files.createDirectories(getTestFolder().resolve("sub").resolve("sub2"));
		var g = getGroupWithFile(Files.copy(TestFileHelper.getTestResource("series/series_08.ARW"), sub.resolve("file.ARW")), sub);
		assertTrue(SortingHelper.pathMatches(g, sub, getTestFolder()));
	}
	
	@Test
	public void testPathMatchesAbsoluteTwoLevelsDeepFileName() throws IOException {
		Files.createFile(getTestFolder().resolve(".notagroup"));
		var sub = Files.createDirectories(getTestFolder().resolve("sub").resolve("sub2"));
		var g = getGroupWithFile(Files.copy(TestFileHelper.getTestResource("series/series_08.ARW"), sub.resolve("file.ARW")), sub);
		assertTrue(SortingHelper.pathMatches(g, sub.resolve("file.ARW"), getTestFolder()));
	}
}
