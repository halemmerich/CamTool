package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;

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
		
		new Sort().perform(context);

		assertEquals(4, Files.list(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve("normal")).count());
		
	}
}
