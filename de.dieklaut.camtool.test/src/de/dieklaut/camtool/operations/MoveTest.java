package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

	private long time;

	@Before
	public void setUp() throws IOException {
		time = Calendar.getInstance().getTimeInMillis();
	}
	
	private Path createFile(String name, long timestamp) throws IOException {
		return Files.setLastModifiedTime(Files.createFile(getTestFolder().resolve(name)), FileTime.fromMillis(timestamp));
	}
	
	@Test
	public void testMoveSubSubFolder() throws IOException {
		createFile("file1.ARW", time);
		createFile("file1.JPG", time);
		createFile("file2.ARW", time + 5000);
		String timestamp_file3 = FileUtils.getTimestamp(createFile("file3.JPG", time + 7500));
		
		context = Context.create(getTestFolder());
		new Init().perform(context);
		Sort sort = new Sort(SORTER);
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
		createFile("file1.ARW", time);
		createFile("file1.JPG", time);
		createFile("file2.ARW", time + 2500);
		String timestamp_file3 = FileUtils.getTimestamp(createFile("file3.JPG", time + 5000));
		String timestamp_file4 = FileUtils.getTimestamp(createFile("file4.JPG", time + 6000));
		
		context = Context.create(getTestFolder());

		new Init().perform(context);
		
		Sort sort = new Sort(SORTER);
		sort.setDetectSeries(true);
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
		move.setTargetPath(Paths.get(".."));
		move.perform(context);

		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve("sub").resolve(timestamp_file3 + "_file3.JPG")));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve("sub").resolve(timestamp_file4 + "_file4.JPG")));
		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(subfolder).resolve(testGroupName).resolve(timestamp_file3 + "_file3.JPG")));
		
	}
	
	@Test
	public void testMoveFromMainToFolder() throws IOException {
		createFile("file1.ARW", time);
		createFile("file1.JPG", time);
		createFile("file2.ARW", time + 5000);
		String timestamp_file3 = FileUtils.getTimestamp(createFile("file3.JPG", time + 7500));
		
		context = Context.create(getTestFolder());
		new Init().perform(context);
		Sort sort = new Sort(SORTER);
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
