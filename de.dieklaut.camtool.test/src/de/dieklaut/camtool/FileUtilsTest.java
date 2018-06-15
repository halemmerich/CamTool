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
	public void deleteRecursiveTest() throws IOException {
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
	public void testGetNamePortion() {
		assertEquals("asdf", FileUtils.getNamePortion(Paths.get("asdf").resolve("12345_asdf.arw")));
		assertEquals("asdf", FileUtils.getNamePortion(Paths.get("asdf").resolve("12345_asdf")));
	}
	
	@Test
	public void testGetTimestampPortion() {
		assertEquals(12345, FileUtils.getTimestampPortion(Paths.get("asdf").resolve("12345_asdf.arw")));
		assertEquals(12345, FileUtils.getTimestampPortion(Paths.get("asdf").resolve("12345_asdf")));
	}
	
	@Test
	public void testGetCreationDate() {
		assertEquals(1499505230000l, FileUtils.getCreationDate(TestFileHelper.getTestResource("A7II.ARW")).toEpochMilli());
		assertEquals(1499503995000l, FileUtils.getCreationDate(TestFileHelper.getTestResource("NEX5R.ARW")).toEpochMilli());
		assertEquals(1501103070993l, FileUtils.getCreationDate(TestFileHelper.getTestResource("AVCHD.MTS")).toEpochMilli());
		assertEquals(1501103071000l, FileUtils.getCreationDate(TestFileHelper.getTestResource("XAVC.MP4")).toEpochMilli());
	}
	
	@Test
	public void testGetTimestamp() {
		assertEquals("20170708091350000", FileUtils.getTimestamp(FileUtils.getCreationDate(TestFileHelper.getTestResource("A7II.ARW"))));
		assertEquals("20170708085315000", FileUtils.getTimestamp(FileUtils.getCreationDate(TestFileHelper.getTestResource("NEX5R.ARW"))));
		assertEquals("20170726210430993", FileUtils.getTimestamp(FileUtils.getCreationDate(TestFileHelper.getTestResource("AVCHD.MTS"))));
		assertEquals("20170726210431000", FileUtils.getTimestamp(FileUtils.getCreationDate(TestFileHelper.getTestResource("XAVC.MP4"))));
	}
	
	@Test
	public void testRemoveSuffix() {
		assertEquals("test.jpg",FileUtils.removeSuffix("test.jpg.arw"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveSuffixNoDot() {
		FileUtils.removeSuffix("test");
	}
}
