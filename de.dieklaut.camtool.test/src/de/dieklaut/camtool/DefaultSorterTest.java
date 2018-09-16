package de.dieklaut.camtool;
 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

import de.dieklaut.camtool.renderjob.RenderJavaScriptScriptMultiRenderJob;

public class DefaultSorterTest extends FileBasedTest {
	
	private static final DefaultSorter SORTER = new DefaultSorter();

	@Test
	public void testIdentifyGroupsComplexStructure() throws IOException {
		Files.createFile(getTestFolder().resolve(Constants.SORTED_FILE_NAME));
		
		Files.createFile(getTestFolder().resolve("file1.ARW"));
		Files.createFile(getTestFolder().resolve("file1.JPG"));

		Files.createFile(getTestFolder().resolve("file2.ARW"));
		Files.createFile(getTestFolder().resolve("file3.ARW"));
		Files.createFile(getTestFolder().resolve("file4.ARW"));

		Path subdir = Files.createDirectory(getTestFolder().resolve("subdir"));
		Files.createFile(subdir.resolve("file5.ARW"));
		Files.createFile(subdir.resolve("file5.JPG"));
		Files.createFile(subdir.resolve("file6.ARW"));
		Files.createFile(subdir.resolve("file6.JPG"));
		
		Collection<Group> sorting = SORTER.identifyGroups(getTestFolder());
		assertEquals(5, sorting.size());
	}

	@Test
	public void testIdentifySingleGroups() throws IOException {
		Files.createFile(getTestFolder().resolve("file1.ARW"));
		Files.createFile(getTestFolder().resolve("file1.JPG"));

		Files.createFile(getTestFolder().resolve("file2.ARW"));
		Files.createFile(getTestFolder().resolve("file3.ARW"));
		
		Collection<Group> sorting = SORTER.identifyGroups(getTestFolder());
		assertEquals(3, sorting.size());
		for (Group current : sorting) {
			assertThat(current, new IsInstanceOf(SingleGroup.class));
		}
	}

	@Test
	public void testIdentifyMultiGroups() throws IOException {
		Path subdir = Files.createDirectory(getTestFolder().resolve("subdir"));
		Files.createFile(subdir.resolve("file1.ARW"));
		Files.createFile(subdir.resolve("file1.JPG"));


		Path subdir2 = Files.createDirectory(getTestFolder().resolve("subdir2"));
		Files.createFile(subdir2.resolve("file2.ARW"));
		Files.createFile(subdir2.resolve("file3.ARW"));
		
		Collection<Group> sorting = SORTER.identifyGroups(getTestFolder());
		assertEquals(2, sorting.size());
		for (Group current : sorting) {
			assertThat(current, new IsInstanceOf(MultiGroup.class));
		}
	}

	@Test
	public void testIdentifyGroupsDeepStructure() throws IOException {
		Files.createFile(getTestFolder().resolve("file1.ARW"));
		Files.createFile(getTestFolder().resolve("file1.JPG"));

		Path subdir = Files.createDirectory(getTestFolder().resolve("subdir"));
		Files.createFile(subdir.resolve("file5.ARW"));
		Files.createFile(subdir.resolve("file5.JPG"));
		Files.createFile(subdir.resolve("file6.ARW"));
		Files.createFile(subdir.resolve("file6.JPG"));

		Path subdir2 = Files.createDirectory(subdir.resolve("subdir2"));
		Files.createFile(subdir2.resolve("file2.ARW"));
		Files.createFile(subdir2.resolve("file3.ARW"));
		Files.createFile(subdir2.resolve("file4.ARW"));
		
		Collection<Group> sorting = SORTER.identifyGroups(getTestFolder());
		assertEquals(2, sorting.size());
	}
	
	@Test
	public void testIdentifyGroupsCombineFolderToMulti() throws IOException {
		Path subFolder = getTestFolder().resolve("sub");
		
		Files.createDirectories(subFolder);
		
		Files.createFile(subFolder.resolve("file1.ARW"));
		Files.createFile(subFolder.resolve("file1.JPG"));
		Files.createFile(subFolder.resolve("file2.ARW"));
		Files.createFile(subFolder.resolve("file2.JPG"));
		Files.createFile(subFolder.resolve("file3.ARW"));
		Files.createFile(subFolder.resolve("file3.JPG"));
		Files.createFile(subFolder.resolve("file4.ARW"));
		Files.createFile(subFolder.resolve("file4.JPG"));
		
		Collection<Group> sorting = SORTER.identifyGroups(getTestFolder());

		assertEquals(1, sorting.size());

		boolean foundMulti = false;
		boolean foundSingle = false;
		
		for (Group g : sorting) {
			if (g instanceof MultiGroup) {
				foundMulti = true;
				assertEquals(8, g.getAllFiles().size());
				assertEquals("sub", g.getName());
			}
			if (g instanceof SingleGroup) {
				foundSingle = true;
			}
		}
		
		assertTrue(foundMulti);
		assertFalse(foundSingle);
	}

	@Test
	public void testIdentifyGroupsSingleGroup() throws IOException {
		Path file1arw = Files.createFile(getTestFolder().resolve("file1.ARW"));
		Path file1jpg = Files.createFile(getTestFolder().resolve("file1.JPG"));
		Path file1arwpp3 = Files.createFile(getTestFolder().resolve("file1.ARW.pp3"));
		Path file1jpgpp3 = Files.createFile(getTestFolder().resolve("file1.JPG.pp3"));

		Collection<Group> sorting = SORTER.identifyGroups(getTestFolder());
		assertEquals(1, sorting.size());
		Group group = sorting.iterator().next();
		Collection<Path> files = group.getAllFiles();

		assertEquals(4, files.size());
		assertTrue(files.contains(file1arw));
		assertTrue(files.contains(file1jpg));
		assertTrue(files.contains(file1arwpp3));
		assertTrue(files.contains(file1jpgpp3));
	}
	
	@Test
	public void testIdentifyGroupsCombineSeriesSingleFile() throws IOException {
		List<Path> arw = new LinkedList<Path>();
		
		for (int i = 6; i <= 9; i++) {
			String filename = String.format("series_%02d.ARW", i);
			String sourcepath = "series/" + filename;
			arw.add(Files.copy(TestFileHelper.getTestResource(sourcepath), getTestFolder().resolve(filename)));
		}

		Collection<Group> sorting = SORTER.identifyGroups(getTestFolder());
		
		SortingHelper.combineSeries(sorting, 2);
		
		assertEquals(1, sorting.size());
		Group group = sorting.iterator().next();
		
		assertThat(group, new IsInstanceOf(MultiGroup.class));
		
		MultiGroup seriesGroup = (MultiGroup) group;
		assertEquals(4, seriesGroup.getAllFiles().size());
	}
	
	@Test
	public void testIdentifyGroupsNoFilesButRenderscript() throws IOException {
		Path subFolder = getTestFolder().resolve("sub");
		
		Files.createDirectories(subFolder);
		
		Files.createFile(subFolder.resolve("camtool_renderscript"));

		Collection<Group> sorting = SORTER.identifyGroups(getTestFolder());
		assertEquals(1, sorting.size());
		Group group = sorting.iterator().next();
		Collection<Path> files = group.getAllFiles();
		assertEquals(0, files.size());
		assertThat(group, new IsInstanceOf(MultiGroup.class));
		assertThat(group.getRenderJob(), new IsInstanceOf(RenderJavaScriptScriptMultiRenderJob.class));
	}
}
