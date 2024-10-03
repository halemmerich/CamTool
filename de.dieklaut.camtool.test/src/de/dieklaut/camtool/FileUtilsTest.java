package de.dieklaut.camtool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
	public void testGetSuffix() {
		assertEquals(".arw.pp3", FileUtils.getSuffix(Paths.get("asdf").resolve("12345_asdf.arw.pp3")));
		assertEquals(".arw", FileUtils.getSuffix(Paths.get("asdf").resolve("12345_asdf.arw")));
		assertEquals("", FileUtils.getSuffix(Paths.get("asdf").resolve("12345_asdf")));
	}
	
	@Test
	public void testGetTimestampPortion() {
		assertEquals("12345", FileUtils.getTimestampPortion(Paths.get("asdf").resolve("12345_asdf.arw.pp3")));
		assertEquals("12345", FileUtils.getTimestampPortion(Paths.get("asdf").resolve("12345_asdf.arw")));
		assertEquals("12345", FileUtils.getTimestampPortion(Paths.get("asdf").resolve("12345_asdf")));
	}
	
	@Test
	public void testGetCreationDate() {
		assertEquals(1716840473703l, FileUtils.getCreationDate(Paths.get("IMG_20240527_200753_703.jpg")).toEpochMilli());
		assertEquals(1716840473000l, FileUtils.getCreationDate(Paths.get("IMG_20240527_200753.jpg")).toEpochMilli());
		assertEquals(1499505230000l, FileUtils.getCreationDate(TestFileHelper.getTestResource("A7II.ARW")).toEpochMilli());
		assertEquals(1499503995000l, FileUtils.getCreationDate(TestFileHelper.getTestResource("NEX5R.ARW")).toEpochMilli());
		//assertEquals(1501103070993l, FileUtils.getCreationDate(TestFileHelper.getTestResource("AVCHD.MTS")).toEpochMilli());
		//assertEquals(1501103071000l, FileUtils.getCreationDate(TestFileHelper.getTestResource("XAVC.MP4")).toEpochMilli());
		assertEquals(0l, FileUtils.getCreationDate(Paths.get("19700101000000000_XAVC.MP4")).toEpochMilli());
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
		assertEquals("20190104091144948", FileUtils.getTimestamp(FileUtils.getCreationDate(TestFileHelper.getTestResource("AVCHD.MTS"))));
		assertEquals("20190104091144978", FileUtils.getTimestamp(FileUtils.getCreationDate(TestFileHelper.getTestResource("XAVC.MP4"))));
		assertEquals("20200823125203000", FileUtils.getTimestamp(FileUtils.getCreationDate(TestFileHelper.getTestResource("DiffOrigDate.JPG"))));
	}
	
	@Test
	public void testGetInstant() throws ParseException {
		Instant now = Instant.ofEpochMilli(123412341234l);
		assertEquals(now, FileUtils.getInstant(FileUtils.getTimestamp(now)));
	}
	
	@Test
	public void testRemoveSuffix() {
		assertEquals("test.jpg",FileUtils.removeSuffix("test.jpg.arw"));
	}
	
	public void testRemoveSuffixNoDot() {
		assertEquals("test",FileUtils.removeSuffix("test"));
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

		assertEquals("1973464274", FileUtils.getChecksum(paths));
		assertEquals("1973464274", FileUtils.getChecksum(pathsReversed));
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
	
	@Test
	public void testMoveRecursive() throws IOException {
		Path source = Files.createDirectories(getTestFolder().resolve("source"));
		Path destination = Files.createDirectories(getTestFolder().resolve("dest"));
		Path linktarget = Files.createFile(getTestFolder().resolve("linktarget"));
		
		Path testfile = Files.createFile(source.resolve("testfile"));
		Path link = Files.createSymbolicLink(source.resolve("link"), linktarget);
		Path testdir = Files.createDirectories(source.resolve("testdir"));
		Path subfile = Files.createFile(testdir.resolve("sub"));
		Path sublink = Files.createSymbolicLink(testdir.resolve("sublink"), testdir.relativize(linktarget));
		
		FileUtils.moveRecursive(source, destination);

		assertTrue(Files.exists(destination.resolve(testfile.getFileName())));
		assertTrue(Files.exists(destination.resolve(testdir.getFileName()).resolve(subfile.getFileName())));
		assertEquals("../linktarget", Files.readSymbolicLink(destination.resolve(link.getFileName())).toString());
		assertEquals("../../linktarget", Files.readSymbolicLink(destination.resolve(testdir.getFileName()).resolve(sublink.getFileName())).toString());
	}
	
	@Test
	public void testDeleteEverythingBut() throws IOException {
		Path dir = Files.createDirectories(getTestFolder().resolve("testdir"));
		Path toBeDeleted = Files.createFile(dir.resolve("toBeDeleted"));
		Path toStay = Files.createFile(dir.resolve("toStay"));
		Set<Path> keep = new HashSet<>();
		keep.add(toStay);
		
		FileUtils.deleteEverythingBut(dir, keep);

		assertTrue(Files.exists(toStay));
		assertFalse(Files.exists(toBeDeleted));
	}
	
	@Test
	public void testDeleteEverythingButRecursive() throws IOException {
		Path dir = Files.createDirectories(getTestFolder().resolve("testdir"));
		Path subdir = Files.createDirectories(dir.resolve("subdir"));
		Path subdirToBeDeleted = Files.createDirectories(dir.resolve("subdirToBeDeleted"));
		Path toBeDeleted = Files.createFile(subdir.resolve("toBeDeleted"));
		Path toStay = Files.createFile(dir.resolve("toStay"));
		Path toStaySub = Files.createFile(subdir.resolve("toStay"));
		Path toBeDeletedSub = Files.createFile(subdirToBeDeleted.resolve("toBeDeleted"));
		
		Set<Path> keep = new HashSet<>();
		keep.add(toStay);
		keep.add(toStaySub);
		
		FileUtils.deleteEverythingBut(dir, keep);

		assertTrue(Files.exists(subdir));
		assertTrue(Files.exists(toStay));
		assertTrue(Files.exists(toStaySub));
		assertFalse(Files.exists(subdirToBeDeleted));
		assertFalse(Files.exists(toBeDeleted));
		assertFalse(Files.exists(toBeDeletedSub));
	}
	
	@Test
	public void testChangeLinkTargetFilename() throws IOException {
		Path targetfolder = Files.createDirectories(getTestFolder().resolve("targetfolder"));
		Path linkfolder = Files.createDirectories(getTestFolder().resolve("linkfolder"));
		
		Path linktarget = Files.createFile(targetfolder.resolve("linktarget"));
		Path link = Files.createSymbolicLink(linkfolder.resolve("link"), linktarget);
		
		FileUtils.changeLinkTargetFilename(link, "newname");
		
		assertEquals(targetfolder.resolve("newname"), Files.readSymbolicLink(link));
	}
	
	@Test
	public void testRenameFile() throws IOException {
		Path sourcefolder = Files.createDirectories(getTestFolder().resolve("sourcefolder"));
		Path linkfolder = Files.createDirectories(getTestFolder().resolve("linkfolder"));
		Path linkfolder2 = Files.createDirectories(getTestFolder().resolve("linkfolder2"));

		Path linktarget = Files.createFile(sourcefolder.resolve("linktarget"));
		Path link = Files.createSymbolicLink(linkfolder.resolve("20200000000000000_link"), linktarget);
		Path link2 = Files.createSymbolicLink(linkfolder2.resolve("20200000000000000_link"), link);
		
		FileUtils.renameFile(linktarget, getTestFolder(), "newname");

		assertTrue(Files.exists(sourcefolder.resolve("newname")));
		assertEquals(sourcefolder.resolve("newname"), Files.readSymbolicLink(link));
		assertEquals(link, Files.readSymbolicLink(link2));
		
	}
	
	@Test
	public void testUpdateFilenameToLinkTargetname() throws IOException {
		Path linktarget = Files.createFile(getTestFolder().resolve("linktarget"));
		Path linkfolder = Files.createDirectories(getTestFolder().resolve("linkfolder"));
		Path link = Files.createSymbolicLink(linkfolder.resolve("20200000000000000_link"), linktarget);
		
		assertEquals(linktarget.getFileName(), FileUtils.updateFilenameToLinkTargetname(link).getFileName());
		
	}
	
	@Test
	public void testGetByRegex() throws IOException {
		Files.createFile(getTestFolder().resolve("asdf"));
		Path file2 = Files.createFile(getTestFolder().resolve("sdfg"));
		Path file3 = Files.createDirectory(getTestFolder().resolve("dfgh"));
		Files.createFile(getTestFolder().resolve("fghj"));
		
		Collection<Path> result = FileUtils.getByRegex(getTestFolder(), "dfg");
		assertEquals(2, result.size());
		assertTrue(result.contains(file2));
		assertTrue(result.contains(file3));
		
	}
}
