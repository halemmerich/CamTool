package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.TestFileHelper;
import de.dieklaut.camtool.util.FileUtils;

public class InitTest extends FileBasedTest {
	
	String[] fileNames = new String[] { "NEX5R.ARW", "NEX5R.JPG", "A7II.ARW", "A7II.JPG", "noexif.png", "XAVC.MP4",
			"AVCHD.MTS", "neutral.pp3", "neutral_deleted.pp3", "empty.file" };
	String[] timestamps = new String[] { "1499496795", "1499496795", "1499498030", "1499498030", "1500055432", "1501094414", "1501103070",
			"1502471392", "1502471659", "1503427257" };
	Path[] paths = new Path[fileNames.length];
	
	@Test
	public void testFolderCreation() throws IOException {
		for (int i = 0; i < fileNames.length; i++) {
			paths[i] = Files.copy(TestFileHelper.getTestResource(fileNames[i]), getTestFolder().resolve(fileNames[i]));
			timestamps[i] = FileUtils.getTimestamp(paths[i]);
		}
		
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
			String expectedFilename = timestamps[i] + "_" + fileNames[i];
			assertTrue("File " + expectedFilename + " does not exist", Files.exists(context.getTimeLine().resolve(expectedFilename)));
			assertEquals(context.getOriginals().resolve(fileNames[i]).toRealPath(), context.getTimeLine().resolve(timestamps[i] + "_" + fileNames[i]).toRealPath());
		}
	}
	
	@Test
	public void testFolderCreationFromMultipleInputFolders() throws IOException {
		Files.createDirectory(getTestFolder().resolve("sub1"));
		Files.createDirectory(getTestFolder().resolve("sub2"));
		paths[0] = Files.copy(TestFileHelper.getTestResource(fileNames[0]), getTestFolder().resolve("sub1").resolve(fileNames[0]));
		paths[2] = Files.copy(TestFileHelper.getTestResource(fileNames[2]), getTestFolder().resolve("sub2").resolve(fileNames[0]));
		timestamps[0] = FileUtils.getTimestamp(paths[0]);
		timestamps[2] = FileUtils.getTimestamp(paths[2]);
		
		Init init = new Init();
		Context context = Context.create(getTestFolder());
		init.perform(context);
		
		// checks that the timeline was created correctly
		String expectedFilename = timestamps[0] + "_" + fileNames[0];
		assertTrue("File " + expectedFilename + " does exist", Files.exists(context.getTimeLine().resolve(expectedFilename)));
		assertEquals(context.getOriginals().resolve("sub1").resolve(fileNames[0]).toRealPath(), context.getTimeLine().resolve(timestamps[0] + "_" + fileNames[0]).toRealPath());
		expectedFilename = timestamps[2] + "_" + fileNames[0];
		assertTrue("File " + expectedFilename + " does exist", Files.exists(context.getTimeLine().resolve(expectedFilename)));
		assertEquals(context.getOriginals().resolve("sub2").resolve(fileNames[0]).toRealPath(), context.getTimeLine().resolve(timestamps[2] + "_" + fileNames[0]).toRealPath());
		
	}
}
