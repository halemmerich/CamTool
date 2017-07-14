package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import de.dieklaut.camtool.util.FileUtils;

public class FileBasedTest {

	private static Path testFolder;

	@BeforeClass
	public static void setUpClass() throws IOException {
		testFolder = Files.createTempDirectory("test");
	}

	@AfterClass
	public static void tearDownClass() throws IOException, FileOperationException {
		FileUtils.deleteRecursive(testFolder, true);
	}
	
	public Path getTestFolder() {
		return testFolder;
	}
}
