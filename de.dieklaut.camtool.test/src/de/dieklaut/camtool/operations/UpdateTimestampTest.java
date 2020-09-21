package de.dieklaut.camtool.operations;

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

public class UpdateTimestampTest extends FileBasedTest {

	private static final DefaultSorter SORTER = new DefaultSorter();

	@Test
	public void testPerform() throws IOException {		
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("file1.arw"), TestFileHelper.getTestResource("A7II.ARW"));
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("file2.arw"), TestFileHelper.getTestResource("NEX5R.ARW"));
		Path file3 = TestFileHelper.addFileToSorting(context, Paths.get("group/file3.arw"), TestFileHelper.getTestResource("NEX5R.ARW"));
		Path file4 = TestFileHelper.addFileToSorting(context, Paths.get("multi/file4.arw"), TestFileHelper.getTestResource("A7II.ARW"));
		Path file5 = TestFileHelper.addFileToSorting(context, Paths.get("multi/file5.arw"), TestFileHelper.getTestResource("NEX5R.ARW"));

		String modifiedTimestamp = "20200101120000000";
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		Path file6 = Files.createFile(sorting.resolve(FileUtils.buildFileName(modifiedTimestamp, "asdf",".test")));
		

		Files.move(file1, file1.getParent().resolve(FileUtils.buildFileName(modifiedTimestamp, FileUtils.getNamePortion(file1), FileUtils.getSuffix(file1))));
		Files.move(file2, file2.getParent().resolve(FileUtils.buildFileName(modifiedTimestamp, FileUtils.getNamePortion(file2), FileUtils.getSuffix(file2))));
		Path renamedFile3 = Files.move(file3, file3.getParent().resolve(FileUtils.buildFileName(modifiedTimestamp, FileUtils.getNamePortion(file3), FileUtils.getSuffix(file3))));
		Path renamedFile4 = Files.move(file4, file4.getParent().resolve(FileUtils.buildFileName(modifiedTimestamp, FileUtils.getNamePortion(file4), FileUtils.getSuffix(file4))));
		Path renamedFile5 = Files.move(file5, file5.getParent().resolve(FileUtils.buildFileName(modifiedTimestamp, FileUtils.getNamePortion(file5), FileUtils.getSuffix(file5))));
		
		Path profile = TestFileHelper.getTestResource("neutral_deleted.pp3");
		Path profile1 = Files.copy(profile, sorting.resolve(FileUtils.buildFileName(modifiedTimestamp, "file1.pp3")));
		Path profile3 = Files.copy(profile, sorting.resolve("group").resolve(FileUtils.buildFileName(modifiedTimestamp, "file3.pp3")));
		Path profile4 = Files.copy(profile, sorting.resolve("multi").resolve(FileUtils.buildFileName(modifiedTimestamp, "file4.pp3")));
		
		UpdateTimestamp updateTimestamp = new UpdateTimestamp(SORTER);
		updateTimestamp.setName(Constants.DEFAULT_SORTING_NAME);
		updateTimestamp.perform(context);

		assertTrue(Files.exists(sorting.resolve(file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve(FileUtils.buildFileName(FileUtils.getTimestampPortion(file1), FileUtils.getNamePortion(file1), FileUtils.getSuffix(profile1)))));
		assertTrue(Files.exists(sorting.resolve(file2.getFileName())));
		assertTrue(Files.exists(sorting.resolve("group").resolve(renamedFile3.getFileName())));
		assertTrue(Files.exists(sorting.resolve("group").resolve(profile3.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multi").resolve(renamedFile4.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multi").resolve(renamedFile5.getFileName())));
		assertTrue(Files.exists(sorting.resolve("multi").resolve(profile4.getFileName().getFileName())));
		assertTrue(Files.exists(file6));
	}
}
