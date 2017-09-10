package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.FileOperationException;
import de.dieklaut.camtool.util.FileUtils;

public class DeleteUnusedTest extends FileBasedTest {
	
	private Context context;
	private String timestamp_file3;

	@Before
	public void setUp() throws IOException, FileOperationException {
		Files.createFile(getTestFolder().resolve("file1.ARW"));
		Files.createFile(getTestFolder().resolve("file1.JPG"));
		Files.createFile(getTestFolder().resolve("file2.ARW"));
		timestamp_file3 = FileUtils.getTimestamp(Files.createFile(getTestFolder().resolve("file3.JPG")));
		
		context = Context.create(getTestFolder());
		new Init().perform(context);
		
		Sort sort = new Sort();
		sort.perform(context);
		
		Files.delete(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file3 + "_file3.JPG"));
		
		UpdateUnused updateUnused = new UpdateUnused();
		updateUnused.perform(context);
	}
	
	@Test
	public void test() throws IOException {
		
		Path unusedFolder = context.getRoot().resolve(Constants.FOLDER_UNUSED);
		
		new DeleteUnused().perform(context);
		
		assertEquals(0, Files.list(unusedFolder).count());
		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_UNUSED).resolve(timestamp_file3 + "_file3.JPG")));
		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_TIMELINE).resolve(timestamp_file3 + "_file3.JPG")));
		assertFalse(Files.exists(getTestFolder().resolve(Constants.FOLDER_ORIGINAL).resolve("file3.JPG")));
	}
}
