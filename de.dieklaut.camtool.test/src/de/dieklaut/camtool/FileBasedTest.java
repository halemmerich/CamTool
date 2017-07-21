package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;

import de.dieklaut.camtool.util.FileUtils;

public class FileBasedTest {

	private static Path testFolder;

	@Before
	public void setUpClass() throws IOException {
		testFolder = Files.createTempDirectory("test");
	}

	@After
	public void tearDownClass() throws IOException, FileOperationException {
		FileUtils.deleteRecursive(testFolder, true);
	}
	
	public Path getTestFolder() {
		return testFolder;
	}
}
