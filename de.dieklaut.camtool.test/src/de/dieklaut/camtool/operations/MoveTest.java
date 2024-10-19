package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.DefaultSorter;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.TestFileHelper;
import de.dieklaut.camtool.operations.Substitute.Mode;
import de.dieklaut.camtool.util.FileUtils;

public class MoveTest extends FileBasedTest {
	
	private static final Sorter SORTER = new DefaultSorter();
	
	private Context context;

	private long time;

	@Before
	public void setUp() throws IOException {
		time = Calendar.getInstance().getTimeInMillis();
	}
	
	@Test
	public void testMoveToSubFolderByPath() throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("file1.arw"), TestFileHelper.getTestResource("A7II.ARW"));
		
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);

		String targetName = "target";
		
		Move move = new Move(SORTER);
		move.setIdentifiers(Arrays.asList(file1.toString()));
		move.setTargetPath(sorting.resolve(targetName));
		move.perform(context);

		assertFalse(Files.exists(sorting.resolve(file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve(targetName).resolve(file1.getFileName())));
	}
	
	@Test
	public void testMoveToSubFolderWithSubfolders() throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("sub/sub2/file1.arw"), TestFileHelper.getTestResource("A7II.ARW"));
		Files.createDirectories(file1.getParent().getParent().resolve("empty"));
		
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);

		String targetName = "target";
		
		Move move = new Move(SORTER);
		move.setIdentifiers(Arrays.asList("sub"));
		move.setTargetPath(sorting.resolve(targetName));
		move.perform(context);

		assertFalse(Files.exists(file1.getFileName()));
		assertTrue(Files.exists(sorting.resolve(targetName).resolve("sub2").resolve(file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve(targetName).resolve("empty")));
	}
	
	@Test
	public void testMoveFromSubFolderByPath() throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		String targetName = "target";
		
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get(targetName + "/file1.arw"), TestFileHelper.getTestResource("A7II.ARW"));
		
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);

		
		Move move = new Move(SORTER);
		move.setIdentifiers(Arrays.asList(file1.toString()));
		move.setTargetPath(sorting);
		move.perform(context);

		assertTrue(Files.exists(sorting.resolve(file1.getFileName())));
		assertFalse(Files.exists(sorting.resolve(targetName).resolve(file1.getFileName())));
	}
	
	@Test
	public void testMoveSubSubFolder() throws IOException {
		TestFileHelper.createFileWithModifiedDate(getTestFolder().resolve("file1.ARW"), time);
		TestFileHelper.createFileWithModifiedDate(getTestFolder().resolve("file1.JPG"), time);
		TestFileHelper.createFileWithModifiedDate(getTestFolder().resolve("file2.ARW"), time + 5000);
		String timestamp_file3 = FileUtils.getTimestamp(TestFileHelper.createFileWithModifiedDate(getTestFolder().resolve("file3.JPG"), time + 7500));
		
		context = Context.create(getTestFolder());
		new Init().perform(context);
		Sort sort = new Sort(SORTER);
		sort.perform(context);
		
		String testGroupName = "testgroupname";
		String subfolder = "sub";

		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		
		Move move = new Move(SORTER);
		move.setIdentifiers(Arrays.asList(timestamp_file3 + "_file3"));
		move.setTargetPath(sorting.resolve(subfolder).resolve(testGroupName));
		move.perform(context);

		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file3 + "_file3.JPG")));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(subfolder).resolve(testGroupName).resolve(timestamp_file3 + "_file3.JPG")));
	}
	
	@Test
	public void testMoveSubSubFolderMultiGroup() throws IOException {
		String timestamp_file1 = FileUtils.getTimestamp(TestFileHelper.createFileWithModifiedDate(getTestFolder().resolve("file1.ARW"), time));
		TestFileHelper.createFileWithModifiedDate(getTestFolder().resolve("file1.JPG"), time);
		TestFileHelper.createFileWithModifiedDate(getTestFolder().resolve("file2.ARW"), time + 2500);
		String timestamp_file3 = FileUtils.getTimestamp(TestFileHelper.createFileWithModifiedDate(getTestFolder().resolve("file3.JPG"), time + 5000));
		TestFileHelper.createFileWithModifiedDate(getTestFolder().resolve("file4.JPG"), time + 6000);
		
		context = Context.create(getTestFolder());

		new Init().perform(context);
		
		Sort sort = new Sort(SORTER);
		sort.setDetectSeries(true);
		sort.perform(context);
		
		String testGroupName = "testgroupname";
		String subfolder = "sub";

		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		
		Move move = new Move(SORTER);
		move.setIdentifiers(Arrays.asList(timestamp_file1 + "_multi"));
		move.setTargetPath(sorting.resolve(subfolder).resolve(testGroupName));
		move.perform(context);

		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file3 + "_file3.JPG")));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(subfolder).resolve(testGroupName).resolve(timestamp_file3 + "_file3.JPG")));
		
	}

	@Test
	public void testMoveFromMainToFolderAndBack() throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		TestFileHelper.addFileToSorting(context, Paths.get("file1.ARW"), 2000);
		TestFileHelper.addFileToSorting(context, Paths.get("file1.JPG"), 2000);
		TestFileHelper.addFileToSorting(context, Paths.get("file2.ARW"), 7000);
		Path file3 = TestFileHelper.addFileToSorting(context, Paths.get("file3.JPG"), 9500);
			
		String testGroupName = "testgroupname";

		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		
		Move move = new Move(SORTER);
		move.setIdentifiers(Arrays.asList(FileUtils.buildFileName(9500, "file3")));
		move.setTargetPath(sorting.resolve(testGroupName));
		move.perform(context);

		
		assertFalse(Files.exists(sorting.resolve(file3.getFileName())));
		assertTrue(Files.exists(sorting.resolve(testGroupName).resolve(file3.getFileName())));
		
		move = new Move(SORTER);
		move.setIdentifiers(Arrays.asList(FileUtils.buildFileName(9500, "file3")));
		move.setTargetPath(sorting);
		move.perform(context);

		assertFalse(Files.exists(sorting.resolve(testGroupName).resolve(file3.getFileName())));
		assertTrue(Files.exists(sorting.resolve(file3.getFileName())));
	}

	@Test
	public void testMoveFromMainToFolderAndOneLevelBackWithExtraFiles() throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		TestFileHelper.addFileToSorting(context, Paths.get("file1.ARW"), 2000);
		TestFileHelper.addFileToSorting(context, Paths.get("file1.JPG"), 2000);
		TestFileHelper.addFileToSorting(context, Paths.get("file2.ARW"), 7000);
		Path file3 = TestFileHelper.addFileToSorting(context, Paths.get("file3.JPG"), 9500);
			
		String testGroupName = "testgroupname";

		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);

		Path sub = sorting.resolve("sub");
		Path target = sorting.resolve("target");
		Path testgroup = sub.resolve(testGroupName);
		
		Move move = new Move(SORTER);
		move.setIdentifiers(Arrays.asList(FileUtils.buildFileName(9500, "file3")));
		move.setTargetPath(testgroup);
		move.perform(context);
		
		assertFalse(Files.exists(sorting.resolve(file3.getFileName())));
		assertTrue(Files.exists(testgroup.resolve(file3.getFileName())));
		
		//create the additional files
		var empty = Files.createDirectories(testgroup.resolve("empty"));
		var notempty = Files.createDirectories(testgroup.resolve("notempty"));
		var testfile = Files.createFile(testgroup.resolve("notempty").resolve("testfile"));
		
		var substitute = new Substitute();
		substitute.setSortingName(Constants.DEFAULT_SORTING_NAME);
		substitute.setMode(Mode.INTERNAL);
		substitute.setSubstitutions(new String[] { file3.getFileName().toString() });
		substitute.setNameOfGroup(testgroup.toString());
		substitute.perform(context);
		substitute.setMode(Mode.SWITCH);
		substitute.perform(context);
		
		move = new Move(SORTER);
		move.setIdentifiers(Arrays.asList(testGroupName));
		move.setTargetPath(target);
		move.perform(context);

		assertFalse(Files.exists(testgroup.resolve(file3.getFileName())));
		assertFalse(Files.exists(testgroup.resolve(Constants.FILE_NAME_RENDERSUBSTITUTE_EXTERNAL)));
		assertFalse(Files.exists(testgroup.resolve(empty.getFileName())));
		assertFalse(Files.exists(testgroup.resolve(notempty.getFileName())));
		assertFalse(Files.exists(testgroup));
		assertFalse(Files.exists(sub.resolve(file3.getFileName())));
		assertTrue(Files.exists(sorting.resolve(file3.getFileName())));
		assertTrue(Files.exists(target.resolve(file3.getFileName())));
		assertTrue(Files.exists(target.resolve(Constants.FILE_NAME_RENDERSUBSTITUTE_EXTERNAL)));
		assertTrue(Files.exists(target.resolve(empty.getFileName())));
		assertTrue(Files.exists(target.resolve(notempty.getFileName())));
		assertTrue(Files.exists(target.resolve(notempty.getFileName()).resolve(testfile.getFileName())));
	}
	
	@Test
	public void testMoveFromMainRegex() throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		TestFileHelper.addFileToSorting(context, Paths.get("file1.ARW"), 2000);
		TestFileHelper.addFileToSorting(context, Paths.get("file1.JPG"), 2000);
		TestFileHelper.addFileToSorting(context, Paths.get("file2.ARW"), 7000);
		Path file3 = TestFileHelper.addFileToSorting(context, Paths.get("file3.JPG"), 9500);
			
		String testGroupName = "testgroupname";

		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		
		Move move = new Move(SORTER);
		move.setRegex(".*file3.*");
		move.setTargetPath(sorting.resolve(testGroupName));
		move.perform(context);
		
		assertFalse(Files.exists(sorting.resolve(file3.getFileName())));
		assertTrue(Files.exists(sorting.resolve(testGroupName).resolve(file3.getFileName())));
	}
}
