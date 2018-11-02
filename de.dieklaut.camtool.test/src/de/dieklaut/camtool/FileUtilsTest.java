package de.dieklaut.camtool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
		assertEquals(testfile.toRealPath(), newLink.toRealPath());
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
		assertEquals("asdf", FileUtils.getNamePortion(Paths.get("asdf").resolve("12345_asdf.arw.pp3")));
		assertEquals("asdf", FileUtils.getNamePortion(Paths.get("asdf").resolve("12345_asdf.arw")));
		assertEquals("asdf", FileUtils.getNamePortion(Paths.get("asdf").resolve("12345_asdf")));
	}
	
	@Test
	public void testGetTimestampPortion() {
		assertEquals(12345, FileUtils.getTimestampPortion(Paths.get("asdf").resolve("12345_asdf.arw.pp3")));
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
	public void testGetCreationDuration() {
		assertEquals(50, FileUtils.getCreationDuration(TestFileHelper.getTestResource("A7II.ARW")).toMillis());
		assertEquals(25, FileUtils.getCreationDuration(TestFileHelper.getTestResource("NEX5R.ARW")).toMillis());
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
	
	@Test
	public void testGetSimplifiedStringRep() {
		assertEquals("thisisrelative", FileUtils.getSimplifiedStringRep(Paths.get("this/is/relative")));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetChecksumNoInput() {
		FileUtils.getChecksum(Collections.emptySet());
	}

	@Test
	public void testGetChecksum() {
		List<Path> paths = new LinkedList<>();
		paths.add(TestFileHelper.getTestResource("A7II.ARW"));
		paths.add(TestFileHelper.getTestResource("NEX5R.ARW"));
		
		List<Path> pathsReversed = new LinkedList<>();
		pathsReversed.add(TestFileHelper.getTestResource("NEX5R.ARW"));
		pathsReversed.add(TestFileHelper.getTestResource("A7II.ARW"));

		assertEquals("2530393705", FileUtils.getChecksum(paths));
		assertEquals("2530393705", FileUtils.getChecksum(pathsReversed));
	}
	
	@Test
	public void testCopyRecursive() throws IOException {
		Path source = Files.createDirectories(getTestFolder().resolve("source"));
		Path destination = Files.createDirectories(getTestFolder().resolve("dest"));
		Path testfile = Files.createFile(source.resolve("testfile"));
		Path subdir = Files.createDirectory(source.resolve("subdir"));
		Path subfile = Files.createFile(subdir.resolve("subfile"));
		
		FileUtils.copyRecursive(source,destination);

		assertTrue(Files.exists(destination.resolve(testfile.getFileName())));
		assertTrue(Files.exists(destination.resolve(subdir.getFileName())));
		assertTrue(Files.exists(destination.resolve(subdir.getFileName()).resolve(subfile.getFileName())));
		assertEquals(2, Files.list(destination).count());
		assertEquals(1, Files.list(destination.resolve(subdir.getFileName())).count());
	}
	
	@Test
	public void testCopyRecursiveFile() throws IOException {
		Path source = Files.createDirectories(getTestFolder().resolve("source"));
		Path testfile = Files.createFile(source.resolve("testfile"));
		Path destination = Files.createDirectories(getTestFolder().resolve("dest").resolve(testfile.getFileName()));
		
		FileUtils.copyRecursive(source,destination);

		assertTrue(Files.exists(destination.resolve(testfile.getFileName())));
		assertEquals(1, Files.list(destination).count());
	}
	
	@Test
	public void testCopyRecursiveFileToDirectory() throws IOException {
		Path source = Files.createDirectories(getTestFolder().resolve("source"));
		Path testfile = Files.createFile(source.resolve("testfile"));
		Path destination = Files.createDirectories(getTestFolder().resolve("dest"));
		
		FileUtils.copyRecursive(testfile,destination);

		assertTrue(Files.exists(destination.resolve(testfile.getFileName())));
		assertEquals(1, Files.list(destination).count());
	}
	
	@Test(expected = IllegalStateException.class)
	public void testCopyRecursiveDirectoryToFile() throws IOException {
		Path source = Files.createDirectories(getTestFolder().resolve("source"));
		Files.createFile(source.resolve("testfile"));
		Path destination = Files.createFile(getTestFolder().resolve("dest"));
		
		FileUtils.copyRecursive(source,destination);
	}
	
	@Test
	public void testCopyRecursiveNonExistingDestination() throws IOException {
		Path source = Files.createDirectories(getTestFolder().resolve("source"));
		Path destination = getTestFolder().resolve("dest");
		Path testfile = Files.createFile(source.resolve("testfile"));
		Path subdir = Files.createDirectory(source.resolve("subdir"));
		Path subfile = Files.createFile(subdir.resolve("subfile"));
		
		FileUtils.copyRecursive(source,destination);

		assertTrue(Files.exists(destination.resolve(testfile.getFileName())));
		assertTrue(Files.exists(destination.resolve(subdir.getFileName())));
		assertTrue(Files.exists(destination.resolve(subdir.getFileName()).resolve(subfile.getFileName())));
		assertEquals(2, Files.list(destination).count());
		assertEquals(1, Files.list(destination.resolve(subdir.getFileName())).count());
	}
	
	@Test
	public void testHardlinkOrCopy() throws IOException {
		Path source = Files.createDirectories(getTestFolder().resolve("source"));
		Path testfile = Files.createFile(source.resolve("testfile"));
		Path destination = Files.createDirectories(getTestFolder().resolve("dest").resolve(testfile.getFileName()));
		
		FileUtils.hardlinkOrCopy(testfile, destination);

		assertTrue(Files.exists(destination.resolve(testfile.getFileName())));
		assertEquals(1, Files.list(destination).count());
	}
	
	@Test
	public void testHardlinkOrCopyDestFolder() throws IOException {
		Path source = Files.createDirectories(getTestFolder().resolve("source"));
		Path destination = Files.createDirectories(getTestFolder().resolve("dest"));
		Path testfile = Files.createFile(source.resolve("testfile"));
		
		FileUtils.hardlinkOrCopy(testfile, destination);

		assertTrue(Files.exists(destination.resolve(testfile.getFileName())));
		assertEquals(1, Files.list(destination).count());
	}
}
