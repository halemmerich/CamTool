package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;
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
	
	Path profile = TestFileHelper.getTestResource("neutral_deleted.pp3");
	
	/*
	 * Create 2 separate single groups and delete one of them
	 */
	@Test
	public void testPerformDeleteSingleGroup() throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("file1.arw"), TestFileHelper.getTestResource("A7II.ARW"));
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("file2.arw"), TestFileHelper.getTestResource("NEX5R.ARW"));
		
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		//single group
		Path profile1 = Files.copy(profile, sorting.resolve(FileUtils.buildFileName(FileUtils.getTimestamp(file1), "file1.pp3")));
		
		assertTrue(Files.exists(sorting.resolve(file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve(profile1.getFileName())));
		assertTrue(Files.exists(sorting.resolve(file2.getFileName())));
		
		CleanTrash cleanTrash = new CleanTrash(SORTER);
		cleanTrash.setName(Constants.DEFAULT_SORTING_NAME);
		cleanTrash.perform(context);

		assertFalse(Files.exists(sorting.resolve(file1.getFileName())));
		assertFalse(Files.exists(sorting.resolve(profile1.getFileName())));
		assertTrue(Files.exists(sorting.resolve(file2.getFileName())));
	}
	
	/*
	 * Create single group in a folder and delete it
	 */
	@Test
	public void testPerformDeleteGroup() throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path file = TestFileHelper.addFileToSorting(context, Paths.get("group/file3.arw"), TestFileHelper.getTestResource("NEX5R.ARW"));
		
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);

		Path profile3 = Files.copy(profile, sorting.resolve("group").resolve(FileUtils.buildFileName(FileUtils.getTimestamp(file), "file3.pp3")));
		
		assertTrue(Files.exists(sorting.resolve("group").resolve(file.getFileName())));
		assertTrue(Files.exists(sorting.resolve("group").resolve(profile3.getFileName().getFileName())));
		
		CleanTrash cleanTrash = new CleanTrash(SORTER);
		cleanTrash.setName(Constants.DEFAULT_SORTING_NAME);
		cleanTrash.perform(context);

		assertFalse(Files.exists(sorting.resolve("group").resolve(file.getFileName())));
		assertFalse(Files.exists(sorting.resolve("group").resolve(profile3.getFileName().getFileName())));
		assertFalse(Files.exists(sorting.resolve("group")));
	}

	/*
	 * Create a multi group containing two implicit single groups and delete the one of the subgroups
	 */
	@Test
	public void testPerformDeleteMultiGroup2SingleGroups() throws IOException {		
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("multi/file1.arw"), TestFileHelper.getTestResource("A7II.ARW"));
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("multi/file2.arw"), TestFileHelper.getTestResource("NEX5R.ARW"));
		
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
	
		Path profile1 = Files.copy(profile, sorting.resolve("multi").resolve(FileUtils.buildFileName(FileUtils.getTimestamp(file1), "file1.pp3")));
		
		assertTrue(Files.exists(sorting.resolve("multi").resolve(file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multi").resolve(file2.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multi").resolve(profile1.getFileName().getFileName())));
		
		CleanTrash cleanTrash = new CleanTrash(SORTER);
		cleanTrash.setName(Constants.DEFAULT_SORTING_NAME);
		cleanTrash.perform(context);

		assertFalse(Files.exists(sorting.resolve("multi").resolve(file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multi").resolve(file2.getFileName())));
		assertFalse(Files.exists(sorting.resolve("multi").resolve(profile1.getFileName().getFileName())));
	}

	/*
	 * Create a multi group containing two implicit single groups and a internal substitution and delete the substitute file
	 * 
	 * Setup:
	 * 
	 * multisubbed
	 *   file1.arw
	 *   file2.arw
	 *   camtool_rendersubstitute (contains "file1.arw")
	 *   
	 * Expectation:
	 * 
	 * multisubbed
	 *   file2.arw
	 *   
	 */
	@Test
	public void testPerformDeleteMultigroupSubbed() throws IOException {		
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("multisubbed/file1.arw"), TestFileHelper.getTestResource("NEX5R.ARW"));
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("multisubbed/file2.arw"), TestFileHelper.getTestResource("NEX5R.ARW"));
		
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		
		Path deleted_file1 = Files.copy(profile, sorting.resolve("multisubbed").resolve(FileUtils.buildFileName(FileUtils.getTimestamp(file1), "file1.pp3")));
		
		Path sub = TestFileHelper.writeFileToSorting(context, Paths.get("multisubbed/camtool_rendersubstitute"), "file1.arw".getBytes());
		
		assertTrue(Files.exists(sorting.resolve("multisubbed").resolve(file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multisubbed").resolve(file2.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multisubbed").resolve(deleted_file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multisubbed").resolve(sub.getFileName())));
		
		CleanTrash cleanTrash = new CleanTrash(SORTER);
		cleanTrash.setName(Constants.DEFAULT_SORTING_NAME);
		cleanTrash.perform(context);
		
		assertFalse(Files.exists(sorting.resolve("multisubbed").resolve(file1.getFileName())));
		assertFalse(Files.exists(sorting.resolve("multisubbed").resolve(deleted_file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multisubbed").resolve(file2.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multisubbed").resolve(sub.getFileName())));
	}

	/*
	 * multisubbed
	 *   file1.arw
	 *   file1.pp3
	 *   file2.arw
	 *   camtool_rendersubstitute
	 *   camtool_rendersubstitute_external
	 */
	@Test
	public void testPerformDeleteOneFromMultigroupWithExternalSub() throws IOException {		
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("multisubbed/file1.arw"), TestFileHelper.getTestResource("NEX5R.ARW"));
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("multisubbed/file2.arw"), TestFileHelper.getTestResource("NEX5R.ARW"));
		
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		
		Path deleted_file1 = Files.copy(profile, sorting.resolve("multisubbed").resolve(FileUtils.buildFileName(FileUtils.getTimestamp(file1), "file1.pp3")));

		Path sub = TestFileHelper.writeFileToSorting(context, Paths.get("multisubbed/camtool_rendersubstitute"), new byte [0]);
		Path subExt = TestFileHelper.writeFileToSorting(context, Paths.get("multisubbed/camtool_rendersubstitute_external"), "file1.arw".getBytes());
		Files.createLink(sorting.resolve("sub.arw"), file1);

		assertTrue(Files.exists(sorting.resolve("multisubbed").resolve(file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multisubbed").resolve(file2.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multisubbed").resolve(deleted_file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multisubbed").resolve(sub.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multisubbed").resolve(subExt.getFileName())));
		
		CleanTrash cleanTrash = new CleanTrash(SORTER);
		cleanTrash.setName(Constants.DEFAULT_SORTING_NAME);
		cleanTrash.perform(context);
		
		assertFalse(Files.exists(sorting.resolve("multisubbed").resolve(file1.getFileName())));
		assertFalse(Files.exists(sorting.resolve("multisubbed").resolve(deleted_file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multisubbed").resolve(file2.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multisubbed").resolve(sub.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multisubbed").resolve(subExt.getFileName())));
	}

	@Test
	public void testPerformDeleteOneFromMultiGroupWithSubstitute() throws IOException {		
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("group/file1.arw"), TestFileHelper.getTestResource("NEX5R.ARW"));
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("group/file2.arw"), TestFileHelper.getTestResource("NEX5R.ARW"));
		
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		
		Path profile = TestFileHelper.getTestResource("neutral_deleted.pp3");
		Path profile_file2 = Files.copy(profile, sorting.resolve("group").resolve(FileUtils.buildFileName(FileUtils.getTimestamp(file1), "file2.pp3")));
		Path file8 = TestFileHelper.writeFileToSorting(context, Paths.get("group/camtool_rendersubstitute"), "file2.arw".getBytes());

		//group exists
		assertTrue(Files.exists(sorting.resolve("group")));
		assertTrue(Files.exists(sorting.resolve("group").resolve(file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve("group").resolve(file2.getFileName())));
		assertTrue(Files.exists(sorting.resolve("group").resolve(profile_file2.getFileName().getFileName())));
		
		CleanTrash cleanTrash = new CleanTrash(SORTER);
		cleanTrash.setName(Constants.DEFAULT_SORTING_NAME);
		cleanTrash.perform(context);

		//group exists
		assertTrue(Files.exists(sorting.resolve("group")));
		assertTrue(Files.exists(sorting.resolve("group").resolve(file1.getFileName())));
		//substitute file exists
		assertTrue(Files.exists(sorting.resolve("group").resolve(file8.getFileName())));
		//deleted file was removed including pp3
		assertFalse(Files.exists(sorting.resolve("group").resolve(file2.getFileName())));
		assertFalse(Files.exists(sorting.resolve("group").resolve(profile_file2.getFileName().getFileName())));
	}

	@Test
	public void testPerformDeleteAllFromMultiGroupWithSubstitute() throws IOException {		
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("group/file1.arw"), TestFileHelper.getTestResource("NEX5R.ARW"));
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("group/file2.arw"), TestFileHelper.getTestResource("NEX5R.ARW"));
		
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		
		Path profile = TestFileHelper.getTestResource("neutral_deleted.pp3");
		Path profile_file1 = Files.copy(profile, sorting.resolve("group").resolve(FileUtils.buildFileName(FileUtils.getTimestamp(file1), "file1.pp3")));
		Path profile_file2 = Files.copy(profile, sorting.resolve("group").resolve(FileUtils.buildFileName(FileUtils.getTimestamp(file1), "file2.pp3")));
		Path filesub = TestFileHelper.writeFileToSorting(context, Paths.get("group/camtool_rendersubstitute"), "file2.arw".getBytes());

		//group exists
		assertTrue(Files.exists(sorting.resolve("group")));
		assertTrue(Files.exists(sorting.resolve("group").resolve(file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve("group").resolve(file2.getFileName())));
		assertTrue(Files.exists(sorting.resolve("group").resolve(profile_file1.getFileName().getFileName())));
		assertTrue(Files.exists(sorting.resolve("group").resolve(profile_file2.getFileName().getFileName())));
		assertTrue(Files.exists(sorting.resolve("group").resolve("camtool_rendersubstitute")));
		
		CleanTrash cleanTrash = new CleanTrash(SORTER);
		cleanTrash.setName(Constants.DEFAULT_SORTING_NAME);
		cleanTrash.perform(context);

		//group exists, only contains substitute file
		assertTrue(Files.exists(sorting.resolve("group")));
		assertTrue(Files.exists(filesub));
		assertEquals(1, Files.list(sorting.resolve("group")).count());
		
	}
}