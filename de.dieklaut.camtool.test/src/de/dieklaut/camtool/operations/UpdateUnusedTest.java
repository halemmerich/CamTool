package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;
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
import de.dieklaut.camtool.FileOperationException;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.util.FileUtils;

public class UpdateUnusedTest extends FileBasedTest {
	
	private static final Sorter SORTER = new DefaultSorter();
	
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
		
		Sort sort = new Sort(SORTER);
		sort.perform(context);
	}
	
	@Test
	public void test() throws IOException {
		Files.delete(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(timestamp_file3 + "_file3.JPG"));
		
		UpdateUnused updateUnused = new UpdateUnused();
		updateUnused.perform(context);
		
		Path unusedFolder = context.getRoot().resolve(Constants.FOLDER_UNUSED);
		
		assertEquals(1, Files.list(unusedFolder).count());
		assertTrue(Files.exists(getTestFolder().resolve(Constants.FOLDER_UNUSED).resolve(timestamp_file3 + "_file3.JPG")));
	}
}
