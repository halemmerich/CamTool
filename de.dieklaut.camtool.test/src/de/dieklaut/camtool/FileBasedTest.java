package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;

import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.util.FileUtils;

public class FileBasedTest {

	private Path testFolder;

	@Before
	public void setUpTestFolder() throws IOException {
		testFolder = Files.createTempDirectory("test");
		Logger.log("Created test folder " + testFolder, Level.DEBUG);
	}

	@After
	public void tearDownTestFolder() throws IOException {
		FileUtils.deleteRecursive(testFolder, true);
	}
	
	public Path getTestFolder() {
		return testFolder;
	}

	public static SingleGroup getGroupWithFile(Path file, Path root) {
		return new SingleGroup(Arrays.asList(new Path [] { file }), root);
	}
}
