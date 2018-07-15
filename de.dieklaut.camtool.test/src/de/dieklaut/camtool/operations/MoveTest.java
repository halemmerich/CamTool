package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;
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
	public void testMoveFromFolderToMain() throws IOException {
		createFile("file1.ARW", time);
		createFile("file1.JPG", time);
		createFile("file2.ARW", time + 5000);
		String timestamp_file3 = FileUtils.getTimestamp(createFile("file3.JPG", time + 7500));
		
		context = Context.create(getTestFolder());
		new Init().perform(context);
		Sort sort = new Sort(SORTER);
		sort.setMoveAllGroupsToFolder(true);
		sort.perform(context);
		
		Move move = new Move(SORTER);
		move.setNameOfGroup(timestamp_file3 + "_single");
		move.perform(context);
		
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file3 + "_file3.JPG")));
		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file3 + "_single")));
		assertEquals(4, Files.list(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME)).count());
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
		createFile("file1.ARW", time);
		createFile("file1.JPG", time);
		createFile("file2.ARW", time + 2500);
		String timestamp_file3 = FileUtils.getTimestamp(createFile("file3.JPG", time + 5000));
		String timestamp_file4 = FileUtils.getTimestamp(createFile("file4.JPG", time + 6000));
		
		context = Context.create(getTestFolder());

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
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(subfolder).resolve(testGroupName).resolve(timestamp_file3 + "_file3").resolve(timestamp_file3 + "_file3.JPG")));
		
		move = new Move(SORTER);
		move.setNameOfGroup(testGroupName);
		move.setTargetPath(Paths.get("..").resolve(".."));
		move.perform(context);

		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file3 + "_file3").resolve(timestamp_file3 + "_file3.JPG")));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file4 + "_file4").resolve(timestamp_file4 + "_file4.JPG")));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve("testgroupname.camtool_collection")));
		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(subfolder).resolve(testGroupName).resolve(timestamp_file3 + "_file3").resolve(timestamp_file3 + "_file3.JPG")));
		
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
	
	@Test
	public void testMoveMultisFromMainCollectionToFolder() throws IOException {
		String timestamp1 = FileUtils.getTimestamp(createFile("file1.ARW", time));
		createFile("file2.ARW", time);
		String timestamp3 = FileUtils.getTimestamp(createFile("file3.ARW", time + 5000));
		createFile("file4.ARW", time + 5000);
		
		context = Context.create(getTestFolder());
		new Init().perform(context);
		Sort sort = new Sort(SORTER);
		sort.setMoveAllGroupsToFolder(true);
		sort.setDetectSeries(true);
		sort.perform(context);

		String testGroupName = "testgroupname";
		Files.write(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(testGroupName + Constants.FILE_NAME_COLLECTION_SUFFIX), (timestamp1 + "_multi\n" + timestamp3 + "_multi\n").getBytes());
		
		
		Move move = new Move(SORTER);
		move.setNameOfGroup(testGroupName);
		move.setTargetPath(Paths.get(testGroupName));
		move.perform(context);

		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp1 + "_multi")));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(testGroupName).resolve(timestamp1 + "_multi")));
	}
	
	@Test
	public void testMoveFromMainCollectionToFolder() throws IOException {
		Path file1 = createFile("file1.ARW", time);
		Path file1Jpg =createFile("file1.JPG", time);
		Path file2 = createFile("file2.ARW", time + 5000);
		Path file3 = createFile("file3.JPG", time + 7500);
		String timestamp_file1 = FileUtils.getTimestamp(file1);
		String timestamp_file1Jpg = FileUtils.getTimestamp(file1Jpg);
		String timestamp_file2 = FileUtils.getTimestamp(file2);
		String timestamp_file3 = FileUtils.getTimestamp(file3);
		String timestamped_file1 = timestamp_file1 + "_" + file1.getFileName();
		String timestamped_file1Jpg = timestamp_file1Jpg + "_" + file1Jpg.getFileName();
		String timestamped_file2 = timestamp_file2 + "_" + file2.getFileName();
		String timestamped_file3 = timestamp_file3 + "_" + file3.getFileName();
		
		
		context = Context.create(getTestFolder());
		new Init().perform(context);
		Sort sort = new Sort(SORTER);
		sort.setMoveAllGroupsToFolder(false);
		sort.perform(context);
		
		String testGroupName = "testgroupname";
		
		Files.write(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(testGroupName + Constants.FILE_NAME_COLLECTION_SUFFIX), (timestamped_file1 + "\n" + timestamped_file1Jpg + "\n" + timestamped_file2 + "\n" + timestamped_file3 + "\n").getBytes());
		
		Move move = new Move(SORTER);
		move.setNameOfGroup(testGroupName);
		move.setTargetPath(Paths.get(testGroupName));
		move.perform(context);

		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamped_file3)));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(testGroupName).resolve(timestamp_file3 + "_file3").resolve(timestamped_file3)));
		
		move = new Move(SORTER);
		move.setNameOfGroup(testGroupName);
		move.setTargetPath(Paths.get("../"));
		move.perform(context);

		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(testGroupName).resolve(timestamp_file3 + "_file3").resolve(timestamped_file3)));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file3 + "_file3").resolve(timestamped_file3)));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(testGroupName + Constants.FILE_NAME_COLLECTION_SUFFIX)));
	}
	
	@Test
	public void testMoveWithRenderScript() throws IOException {
		Path file1 = createFile("file1.ARW", time);
		String filename = FileUtils.getTimestamp(file1) + "_" + FileUtils.getGroupName(file1);
		
		
		context = Context.create(getTestFolder());
		new Init().perform(context);
		Sort sort = new Sort(SORTER);
		sort.setMoveAllGroupsToFolder(false);
		sort.perform(context);
		
		Path renderScript = Files.createFile(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(filename + Constants.FILE_NAME_RENDERSCRIPT_SUFFIX));
		
		String testGroupName = "testGroup";
		
		Move move = new Move(SORTER);
		move.setNameOfGroup(filename);
		move.setTargetPath(Paths.get(testGroupName));
		move.perform(context);

		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(renderScript.getFileName())));
		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(testGroupName + Constants.FILE_NAME_COLLECTION_SUFFIX)));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(testGroupName).resolve(renderScript.getFileName())));
		
		move = new Move(SORTER);
		move.setNameOfGroup(testGroupName);
		move.setTargetPath(Paths.get("../"));
		move.perform(context);

		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(renderScript.getFileName())));
		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(testGroupName).resolve(renderScript.getFileName())));
	}
}
