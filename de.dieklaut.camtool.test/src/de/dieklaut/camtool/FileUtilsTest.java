package de.dieklaut.camtool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.dieklaut.camtool.util.FileUtils;

public class FileUtilsTest extends FileBasedTest {
	@Test
	public void deleteRecursiveTest() throws IOException, FileOperationException {
		Path testFolder = Files.createTempDirectory("test");

		Path toDelete = Files.createDirectory(Paths.get(testFolder.toString(), "testfolder"));
		Files.createFile(Paths.get(testFolder.toString(), "testfolder", "testfile"));
		FileUtils.deleteRecursive(toDelete, false);
		assertEquals(0, testFolder.toFile().list().length);

		FileUtils.deleteRecursive(testFolder, false);
	}

	@Test
	public void testMoveSymlinkSameLevel() throws IOException {
		Path testfile = Files.createFile(getTestFolder().resolve("testfile"));
		Path subdir = Files.createDirectory(getTestFolder().resolve("subdir"));
		Path subdir2 = Files.createDirectory(getTestFolder().resolve("subdir2"));
		Path link = Files.createSymbolicLink(subdir.resolve("link"), testfile);
		
		Path newLink = FileUtils.moveSymlink(link, subdir2);
		
		assertFalse(Files.exists(link));
		assertEquals(subdir2.resolve("link"), newLink);
	}

	@Test
	public void testMoveSymlinkDifferentLevel() throws IOException {
		Path testfile = Files.createFile(getTestFolder().resolve("testfile"));
		Path subdir = Files.createDirectory(getTestFolder().resolve("subdir"));
		Path link = Files.createSymbolicLink(subdir.resolve("link"), testfile);
		
		Path newLink = FileUtils.moveSymlink(link, getTestFolder());
		
		assertFalse(link.toFile().exists());
		assertEquals(getTestFolder().resolve("link"), newLink);
	}
	
	@Test
	public void testGetTimestampPortion() {
		assertEquals(12345, FileUtils.getTimestampPortion(Paths.get("asdf").resolve("12345_asdf.arw")));
	}
}