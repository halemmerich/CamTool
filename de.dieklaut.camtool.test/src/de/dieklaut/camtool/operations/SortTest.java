
package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.DefaultSorter;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.SingleGroup;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.TestFileHelper;

public class SortTest extends FileBasedTest {
	private static final Sorter SORTER = new DefaultSorter();
	
	@Test
	public void test() throws IOException {
		Files.createFile(getTestFolder().resolve("file1.ARW"));
		Files.createFile(getTestFolder().resolve("file1.JPG"));
		Files.createFile(getTestFolder().resolve("file2.ARW"));
		Files.createFile(getTestFolder().resolve("file3.JPG"));
		
		Context context = Context.create(getTestFolder());
		new Init().perform(context);
		
		Sort sort = new Sort(SORTER);
		sort.perform(context);

		assertEquals(5, Files.list(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve("normal")).count());
		
	}
	@Test
	public void testMoveAllGroups() throws IOException {
		Files.createFile(getTestFolder().resolve("file1.ARW"));
		Files.createFile(getTestFolder().resolve("file1.JPG"));
		Files.createFile(getTestFolder().resolve("file2.ARW"));
		Files.createFile(getTestFolder().resolve("file3.JPG"));
		
		Context context = Context.create(getTestFolder());
		new Init().perform(context);
		
		Sort sort = new Sort(SORTER);
		sort.setMoveAllGroupsToFolder(true);
		sort.perform(context);

		Path sortingFolder = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve("normal");
		assertEquals(4, Files.list(sortingFolder).count());
		assertTrue(Files.exists(sortingFolder.resolve(Constants.SORTED_FILE_NAME)));
		
		Files.list(sortingFolder).forEach(file -> {
			if (!file.getFileName().toString().equals(Constants.SORTED_FILE_NAME)) {
				assertTrue(Files.isDirectory(file));
			}
		});
		
	}
	
	@Test
	public void moveSeries() throws IOException {
		Collection<Group> groups = new LinkedList<>();
		
		Path source = TestFileHelper.getTestResource("A7II.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("A7II.ARW"))));
		
		source = TestFileHelper.getTestResource("series/series_09.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("series_09.ARW"))));
		
		source = TestFileHelper.getTestResource("series/series_07.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("series_07.ARW"))));

		source = TestFileHelper.getTestResource("series/series_06.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("series_06.ARW"))));
		
		source = TestFileHelper.getTestResource("series/series_08.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("series_08.ARW"))));
		
		Context context = Context.create(getTestFolder());
		new Init().perform(context);
		
		Sort sort = new Sort(SORTER);
		sort.setMoveAllGroupsToFolder(true);
		sort.setDetectSeries(true);
		sort.perform(context);

		Path sortingFolder = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve("normal");
		assertEquals(3, Files.list(sortingFolder).count());
		assertTrue(Files.exists(sortingFolder.resolve(Constants.SORTED_FILE_NAME)));
		
		Files.list(sortingFolder).forEach(file -> {
			if (!file.getFileName().toString().equals(Constants.SORTED_FILE_NAME)) {
				assertTrue(Files.isDirectory(file));
			}
		});
	}
	
	@Test
	public void moveOnlySeries() throws IOException {
		Collection<Group> groups = new LinkedList<>();
		
		Path source = TestFileHelper.getTestResource("A7II.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("A7II.ARW"))));
		
		source = TestFileHelper.getTestResource("series/series_09.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("series_09.ARW"))));
		
		source = TestFileHelper.getTestResource("series/series_07.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("series_07.ARW"))));

		source = TestFileHelper.getTestResource("series/series_06.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("series_06.ARW"))));
		
		source = TestFileHelper.getTestResource("series/series_08.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("series_08.ARW"))));
		
		Context context = Context.create(getTestFolder());
		new Init().perform(context);
		
		Sort sort = new Sort(SORTER);
		sort.setMoveAllGroupsToFolder(false);
		sort.setDetectSeries(true);
		sort.perform(context);

		Path sortingFolder = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve("normal");
		assertEquals(7, Files.list(sortingFolder).count());
		assertTrue(Files.exists(sortingFolder.resolve(Constants.SORTED_FILE_NAME)));
		
		int folders [] = new int [] { 0 };
		int files [] = new int [] { 0 };
		int collectionFiles [] = new int [] { 0 };
		
		Files.list(sortingFolder).forEach(file -> {
			if (Files.isDirectory(file)) {
				folders[0]++;
			} else {
				if (file.getFileName().toString().endsWith(Constants.FILE_NAME_COLLECTION_SUFFIX)) {
					collectionFiles[0]++;
				}
				files[0]++;
			}
		});

		assertEquals(0, folders[0]);
		assertEquals(7, files[0]);
		assertEquals(1, collectionFiles[0]);
	}
	
}
