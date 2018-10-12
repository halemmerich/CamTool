package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.DefaultSorter;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.TestFileHelper;
import de.dieklaut.camtool.util.FileUtils;

public class CleanTrashTest extends FileBasedTest {

	private static final DefaultSorter SORTER = new DefaultSorter();

	@Test
	public void testPerform() throws IOException {		
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("file1.arw"), TestFileHelper.getTestResource("A7II.ARW"));
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("file2.arw"), TestFileHelper.getTestResource("NEX5R.ARW"));
		Path file3 = TestFileHelper.addFileToSorting(context, Paths.get("group/file3.arw"), TestFileHelper.getTestResource("NEX5R.ARW"));
		
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		
		Path profile = TestFileHelper.getTestResource("neutral_deleted.pp3");
		Path profile1 = Files.copy(profile, sorting.resolve(FileUtils.buildFileName(FileUtils.getTimestamp(file1), "file1.pp3")));
		Path profile3 = Files.copy(profile, sorting.resolve("group").resolve(FileUtils.buildFileName(FileUtils.getTimestamp(file1), "file3.pp3")));
		
		assertTrue(Files.exists(sorting.resolve(file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve(profile1.getFileName())));
		assertTrue(Files.exists(sorting.resolve(file2.getFileName())));
		assertTrue(Files.exists(sorting.resolve("group").resolve(file3.getFileName())));
		assertTrue(Files.exists(sorting.resolve("group").resolve(profile3.getFileName().getFileName())));
		
		CleanTrash cleanTrash = new CleanTrash(SORTER);
		cleanTrash.setName(Constants.DEFAULT_SORTING_NAME);
		cleanTrash.perform(context);

		assertFalse(Files.exists(sorting.resolve(file1.getFileName())));
		assertFalse(Files.exists(sorting.resolve(profile1.getFileName())));
		assertTrue(Files.exists(sorting.resolve(file2.getFileName())));
		assertFalse(Files.exists(sorting.resolve("group").resolve(file3.getFileName())));
		assertFalse(Files.exists(sorting.resolve("group").resolve(profile3.getFileName().getFileName())));
		assertFalse(Files.exists(sorting.resolve("group")));
	}
}
