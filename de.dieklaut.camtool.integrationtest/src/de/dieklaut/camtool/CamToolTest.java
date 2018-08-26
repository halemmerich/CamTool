package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import de.dieklaut.camtool.util.FileUtils;

public class CamToolTest extends FileBasedTest {

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

		Files.list(Paths.get("")).forEach(currentPath -> FileUtils.deleteRecursive(currentPath, true));
	}

	@Test
	public void testRenderNoArgs() throws IOException {
		String[] fileNames = new String[] { "NEX5R.ARW", "NEX5R.JPG", "A7II.ARW", "A7II.JPG", "noexif.png", "XAVC.MP4",
				"AVCHD.MTS", "neutral.pp3", "neutral_deleted.pp3", "empty.file" };
		Path[] paths = new Path[fileNames.length];
		
		Files.list(Paths.get("")).forEach(currentPath -> FileUtils.deleteRecursive(currentPath, false));
		for (int i = 0; i < fileNames.length; i++) {
			paths[i] = Files.copy(TestFileHelper.getTestResource(fileNames[i]), Paths.get(fileNames[i]));
		}
		
		CamTool.main(new String[] { "init" });
		CamTool.main(new String[] { "sort" });
		CamTool.main(new String[] { "showGroups" });
		CamTool.main(new String[] { "render" });
	}

	@Test
	public void testRenderWithRenderScript() throws IOException {
		for (int i = 6; i < 9; i++) {
			String file = "series_0" + i + ".JPG";
			String path = "series/" + file;
			Files.copy(TestFileHelper.getTestResource(path), Paths.get(file));
		}
		
		CamTool.main(new String[] { "init" });
		CamTool.main(new String[] { "sort", "-c", "-s" });
		
		Files.copy(TestFileHelper.getTestResource("scripts/dri.js"), Paths.get(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve("20170915165451000_multi").resolve(Constants.FILE_NAME_RENDERSCRIPT));
		
		CamTool.main(new String[] { "render" });
	}
}
