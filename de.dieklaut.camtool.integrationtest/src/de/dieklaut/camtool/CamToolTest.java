package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import de.dieklaut.camtool.util.FileUtils;

public class CamToolTest extends FileBasedTest {

	String[] fileNames = new String[] { "NEX5R.ARW", "NEX5R.JPG", "A7II.ARW", "A7II.JPG", "noexif.png", "XAVC.MP4",
			"AVCHD.MTS", "neutral.pp3", "neutral_deleted.pp3", "empty.file" };
	Path[] paths = new Path[fileNames.length];

	@Before
	public void setUp() throws InterruptedException, IOException {
		if (System.getenv("NEEDS_WORKING_DIR") == null) {
			System.err.println(
					"DO NOT EXECUTE THIS TEST WITHOUT SETTING A DEDICATED WORKING DIRECTORY!\nIT WILL DESTROY YOUR STUFF!\n\n!!!!ALL CONTENTS OF THE WORKING DIRECTORY WILL BE DELETED!!!!");
			System.exit(1);
		}
		Files.list(Paths.get("")).forEach(currentPath -> FileUtils.deleteRecursive(currentPath, false));
		for (int i = 0; i < fileNames.length; i++) {
			paths[i] = Files.copy(TestFileHelper.getTestResource(fileNames[i]), Paths.get(fileNames[i]));
		}
	}

	@Test
	public void testRenderNoArgs() {
		CamTool.main(new String[] { "init" });
		CamTool.main(new String[] { "sort" });
		CamTool.main(new String[] { "render" });
	}
}
