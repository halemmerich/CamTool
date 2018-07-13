package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.DefaultSorter;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.util.FileUtils;

public class MoveTest extends FileBasedTest {
	
	private static final Sorter SORTER = new DefaultSorter();
	
	private Context context;
	private String timestamp_file3;

	@Before
	public void setUp() throws IOException {
		long time = Calendar.getInstance().getTimeInMillis();
		Files.createFile(getTestFolder().resolve("file1.ARW"));
		Files.setLastModifiedTime(getTestFolder().resolve("file1.ARW"), FileTime.fromMillis(time - 5000));
		Files.createFile(getTestFolder().resolve("file1.JPG"));
		Files.setLastModifiedTime(getTestFolder().resolve("file1.JPG"), FileTime.fromMillis(time - 5000));
		Files.createFile(getTestFolder().resolve("file2.ARW"));
		Files.setLastModifiedTime(getTestFolder().resolve("file2.ARW"), FileTime.fromMillis(time - 2500));
		timestamp_file3 = FileUtils.getTimestamp(Files.createFile(getTestFolder().resolve("file3.JPG")));
		
		context = Context.create(getTestFolder());
	}
	
	@Test
	public void testMoveFromFolderToMain() throws IOException {
		new Init().perform(context);
		Sort sort = new Sort(SORTER);
		sort.setMoveAllGroupsToFolder(true);
		sort.perform(context);
		
		Move move = new Move(SORTER);
		move.setNameOfGroup(timestamp_file3 + "_single");
		move.perform(context);
		
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file3 + "_file3.JPG")));
		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file3 + "_single")));
		assertEquals(5, Files.list(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME)).count());
	}
	
	@Test
	public void testMoveSubSubFolder() throws IOException {
		new Init().perform(context);
		Sort sort = new Sort(SORTER);
		sort.setMoveAllGroupsToFolder(false);
		sort.perform(context);
		
		String testGroupName = "testgroupname";
		String subfolder = "sub";
		
		Move move = new Move(SORTER);
		move.setNameOfGroup(timestamp_file3 + "_file3");
		move.setTargetPath(Paths.get(subfolder).resolve(testGroupName));
		move.perform(context);

		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file3 + "_file3.JPG")));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(subfolder).resolve(testGroupName).resolve(timestamp_file3 + "_file3.JPG")));
		
		move = new Move(SORTER);
		move.setNameOfGroup(testGroupName);
		move.setTargetPath(Paths.get("..").resolve(".."));
		move.perform(context);

		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file3 + "_file3.JPG")));
		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(subfolder).resolve(testGroupName).resolve(timestamp_file3 + "_file3.JPG")));
	}
	
	@Test
	public void testMoveSubSubFolderMultiGroup() throws IOException {
		String timestamp_file4 = FileUtils.getTimestamp(Files.createFile(getTestFolder().resolve("file4.JPG")));

		new Init().perform(context);
		
		Sort sort = new Sort(SORTER);
		sort.setDetectSeries(true);
		sort.setMoveAllGroupsToFolder(true);
		sort.perform(context);
		
		String testGroupName = "testgroupname";
		String subfolder = "sub";
		
		Move move = new Move(SORTER);
		move.setNameOfGroup(timestamp_file3 + "_multi");
		move.setTargetPath(Paths.get("..").resolve(subfolder).resolve(testGroupName));
		move.perform(context);

		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file3 + "_file3.JPG")));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(subfolder).resolve(testGroupName).resolve(timestamp_file3 + "_file3.JPG")));
		
		move = new Move(SORTER);
		move.setNameOfGroup(testGroupName);
		move.setTargetPath(Paths.get("..").resolve(".."));
		move.perform(context);

		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file3 + "_file3.JPG")));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file4 + "_file4.JPG")));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve("testgroupname.camtool_collection")));
		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(subfolder).resolve(testGroupName).resolve(timestamp_file3 + "_file3.JPG")));
	}
	
	@Test
	public void testMoveFromMainToFolder() throws IOException {
		new Init().perform(context);
		Sort sort = new Sort(SORTER);
		sort.setMoveAllGroupsToFolder(false);
		sort.perform(context);
		
		String testGroupName = "testgroupname";
		
		Move move = new Move(SORTER);
		move.setNameOfGroup(timestamp_file3 + "_file3");
		move.setTargetPath(Paths.get(testGroupName));
		move.perform(context);

		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file3 + "_file3.JPG")));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(testGroupName).resolve(timestamp_file3 + "_file3.JPG")));
	}
}
