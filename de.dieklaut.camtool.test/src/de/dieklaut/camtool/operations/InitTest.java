package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.Before;
import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.FileBasedTest;

public class InitTest extends FileBasedTest {

	String[] fileNames = new String[] { "File1.asdf", "File2.bla", "File3" };
	long[] timestamps = new long[fileNames.length];
	Path [] paths = new Path[fileNames.length];

	@Before
	public void setUp() throws IOException {
		for (int i = 0; i < fileNames.length; i++) {
			paths[i] = Files.createFile(getTestFolder().resolve(fileNames[i]));
			timestamps[i] = Files.readAttributes(getTestFolder().resolve(fileNames[i]), BasicFileAttributes.class).creationTime().toInstant().toEpochMilli();
		}
	}
	
	@Test
	public void testFolderCreation() throws IOException {
		Init init = new Init();
		Context context = Context.create(getTestFolder());
		init.perform(context);

		assertTrue(context.getRoot().equals(getTestFolder()));
		// expects originals and timeline folder
		assertTrue(getTestFolder().resolve(Constants.AUTOMATION_FILE_NAME).toFile().exists());
		assertEquals(3, getTestFolder().toFile().list().length);

		// checks that all files are moved
		assertEquals(getTestFolder().resolve(Constants.FOLDER_ORIGINAL), context.getOriginals());
		for (String current : fileNames) {
			assertFalse(getTestFolder().resolve(current).toFile().exists());
			assertTrue(getTestFolder().resolve(Constants.FOLDER_ORIGINAL).resolve(current).toFile().exists());
		}

		assertEquals(getTestFolder().resolve(Constants.FOLDER_TIMELINE), context.getTimeLine());
		// checks that the timeline was created
		for (int i = 0; i < fileNames.length; i++) {
			assertTrue(Files.exists(context.getTimeLine().resolve(Long.toString(timestamps[i]) + "_" + fileNames[i])));
			assertEquals(context.getOriginals().resolve(fileNames[i]).toRealPath(), context.getTimeLine().resolve(Long.toString(timestamps[i]) + "_" + fileNames[i]).toRealPath());
		}
	}
	
	//TODO implement test for correct handling of subfolders in original folder
}
