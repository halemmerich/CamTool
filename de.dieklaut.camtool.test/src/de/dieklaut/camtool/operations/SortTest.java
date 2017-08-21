package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.FileBasedTest;

public class SortTest extends FileBasedTest {
	@Test
	public void test() throws IOException {
		Files.createFile(getTestFolder().resolve("file1.ARW"));
		Files.createFile(getTestFolder().resolve("file1.JPG"));
		Files.createFile(getTestFolder().resolve("file2.ARW"));
		Files.createFile(getTestFolder().resolve("file3.JPG"));
		
		Context context = Context.create(getTestFolder());
		new Init().perform(context);
		
		Sort sort = new Sort();
		sort.perform(context);

		assertEquals(4, Files.list(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve("normal")).count());
		
	}
	@Test
	public void testMoveAllGroups() throws IOException {
		Files.createFile(getTestFolder().resolve("file1.ARW"));
		Files.createFile(getTestFolder().resolve("file1.JPG"));
		Files.createFile(getTestFolder().resolve("file2.ARW"));
		Files.createFile(getTestFolder().resolve("file3.JPG"));
		
		Context context = Context.create(getTestFolder());
		new Init().perform(context);
		
		Sort sort = new Sort();
		sort.setMoveAllGroupsToFolder(true);
		sort.perform(context);

		assertEquals(3, Files.list(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve("normal")).count());
		
		Files.list(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve("normal")).forEach(file -> {
			assertTrue(Files.isDirectory(file));
		});
		
	}
}
