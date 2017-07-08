package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.FileBasedTest;

public class InitTest extends FileBasedTest{

	List<Path> filesToMove;
	
	@Before
	public void setUp() throws IOException {
		filesToMove = new LinkedList<>();
		filesToMove.add(Files.createFile(Paths.get(getTestFolder().toString(), "File1")));
		filesToMove.add(Files.createFile(Paths.get(getTestFolder().toString(), "File2")));
		filesToMove.add(Files.createFile(Paths.get(getTestFolder().toString(), "File3")));
	}
	
	@Test
	public void testFolderCreation() throws IOException {
		Init init = new Init();
		init.perform(new Context(getTestFolder()));
		assertEquals(1, getTestFolder().toFile().list().length);
		for (Path current : filesToMove) {
			assertFalse(current.toFile().exists());
			Path moved = current.getFileName();
			assertTrue(Paths.get(getTestFolder().toString(), Constants.FOLDER_ORIGINAL, moved.toString()).toFile().exists());
		}
	}
}
