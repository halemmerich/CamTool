
package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;
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
		long time = Calendar.getInstance().getTimeInMillis();
		Files.createFile(getTestFolder().resolve("file1.ARW"));
		Files.setLastModifiedTime(getTestFolder().resolve("file1.ARW"), FileTime.fromMillis(time - 5000));
		Files.createFile(getTestFolder().resolve("file1.JPG"));
		Files.setLastModifiedTime(getTestFolder().resolve("file1.JPG"), FileTime.fromMillis(time - 5000));
		Files.createFile(getTestFolder().resolve("file2.ARW"));
		Files.setLastModifiedTime(getTestFolder().resolve("file2.ARW"), FileTime.fromMillis(time - 2500));
		Files.createFile(getTestFolder().resolve("file3.JPG"));
		
		Context context = Context.create(getTestFolder());
		new Init().perform(context);
		
		Sort sort = new Sort(SORTER);
		sort.perform(context);

		assertEquals(5, Files.list(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve("normal")).count());
		
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
		sort.setDetectSeries(true);
		sort.setDetectSeriesTime(2);
		sort.perform(context);

		Path sortingFolder = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve("normal");
		assertEquals(3, Files.list(sortingFolder).count());
		assertTrue(Files.exists(sortingFolder.resolve(Constants.SORTED_FILE_NAME)));
		

		assertContents(sortingFolder, 1, 2);
		
		Path multi = (Path) Files.list(sortingFolder).filter(current -> Files.isDirectory(current)).toArray()[0];
		assertContents(multi, 0, 4);
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
		sort.setDetectSeries(true);
		sort.setDetectSeriesTime(2);
		sort.perform(context);

		Path sortingFolder = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve("normal");
		assertEquals(3, Files.list(sortingFolder).count());
		assertTrue(Files.exists(sortingFolder.resolve(Constants.SORTED_FILE_NAME)));
		
		assertContents(sortingFolder, 1, 2);
	}

	private void assertContents(Path sortingFolder, int numberOfFolders, int numberOfFiles) throws IOException {
		int folders [] = new int [] { 0 };
		int files [] = new int [] { 0 };
		
		Files.list(sortingFolder).forEach(file -> {
			if (Files.isDirectory(file)) {
				folders[0]++;
			} else {
				files[0]++;
			}
		});

		assertEquals(numberOfFolders, folders[0]);
		assertEquals(numberOfFiles, files[0]);
	}
	
}
