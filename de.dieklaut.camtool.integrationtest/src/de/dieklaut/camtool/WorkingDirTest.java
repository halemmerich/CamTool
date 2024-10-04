package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;

import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.util.FileUtils;

public class WorkingDirTest {


	@Before
	public void setUp() throws InterruptedException, IOException {
		if (System.getenv("NEEDS_WORKING_DIR") == null) {
			System.err.println(
					"DO NOT EXECUTE THIS TEST WITHOUT SETTING A DEDICATED WORKING DIRECTORY!\nIT WILL DESTROY YOUR STUFF!\n\n!!!!ALL CONTENTS OF THE WORKING DIRECTORY WILL BE DELETED!!!!");
			System.exit(1);
		}
		
		if (!Paths.get("").toAbsolutePath().normalize().equals(Paths.get(System.getenv("NEEDS_WORKING_DIR")).toAbsolutePath().normalize())) {
			System.err.println(
					"DO NOT EXECUTE THIS TEST WITHOUT SETTING A CORRECT DEDICATED WORKING DIRECTORY!\nIT WILL DESTROY YOUR STUFF!\n\n!!!!ALL CONTENTS OF THE WORKING DIRECTORY WILL BE DELETED!!!!\n\nENVIRONMENT VARIABLE IS SET BUT INCORRECT!");
			System.exit(1);
		}

		Logger.log("Working in test folder " + Paths.get("").toAbsolutePath(), Level.DEBUG);
		try (var l = Files.list(Paths.get("").toAbsolutePath())){
			l.forEach(currentPath -> FileUtils.deleteRecursive(currentPath, true));
		}
	}
}
