package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.dieklaut.camtool.FileOperationException;
import de.dieklaut.camtool.util.FileUtils;

public class FileUtilsTest {
	@Test
	public void deleteRecursiveTest() throws IOException, FileOperationException {
		Path testFolder = Files.createTempDirectory("test");
		
		Path toDelete = Files.createDirectory(Paths.get(testFolder.toString(), "testfolder"));
		Files.createFile(Paths.get(testFolder.toString(), "testfolder", "testfile"));
		FileUtils.deleteRecursive(toDelete, false);
		assertEquals(0, testFolder.toFile().list().length);
		
		FileUtils.deleteRecursive(testFolder, false);
	}
}
