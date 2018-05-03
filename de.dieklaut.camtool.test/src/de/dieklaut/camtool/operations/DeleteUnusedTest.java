package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.DefaultSorter;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.util.FileUtils;

public class DeleteUnusedTest extends FileBasedTest {
	
	private Context context;
	private String timestamp_file1_arw;
	private String timestamp_file2;
	private String timestamp_file3;

	@Before
	public void setUp() throws IOException {
		timestamp_file1_arw = FileUtils.getTimestamp(Files.createFile(getTestFolder().resolve("file1.ARW")));
		Files.createFile(getTestFolder().resolve("file1.JPG"));
		timestamp_file2 = FileUtils.getTimestamp(Files.createFile(getTestFolder().resolve("file2.ARW")));
		timestamp_file3 = FileUtils.getTimestamp(Files.createFile(getTestFolder().resolve("file3.JPG")));
		
		context = Context.create(getTestFolder());
		new Init().perform(context);
		
		Sort sort = new Sort(new DefaultSorter());
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
		
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_TIMELINE).resolve(timestamp_file1_arw + "_file1.JPG")));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_ORIGINAL).resolve("file1.JPG")));
		
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_TIMELINE).resolve(timestamp_file1_arw + "_file1.ARW")));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_ORIGINAL).resolve("file1.ARW")));
		
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_TIMELINE).resolve(timestamp_file2 + "_file2.ARW")));
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_ORIGINAL).resolve("file2.ARW")));
	}
}
