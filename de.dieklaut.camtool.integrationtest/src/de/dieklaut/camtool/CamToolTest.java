package de.dieklaut.camtool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.dieklaut.camtool.util.FileUtils;

public class CamToolTest extends WorkingDirTest {

	@Test
	public void testRenderNoArgs() throws IOException {
		String[] fileNames = new String[] { "NEX5R.ARW", "NEX5R.JPG", "A7II.ARW", "A7II.JPG", "noexif.png", "XAVC.MP4",
				"AVCHD.MTS", "neutral.pp3", "neutral_deleted.pp3", "neutral_rank3.pp3", "empty.file" };
		Path[] paths = new Path[fileNames.length];
		
		Files.list(Paths.get("")).forEach(currentPath -> FileUtils.deleteRecursive(currentPath, false));
		for (int i = 0; i < fileNames.length; i++) {
			paths[i] = Files.copy(TestFileHelper.getTestResource(fileNames[i]), Paths.get(fileNames[i]));
		}
		
		CamTool.main(new String[] { "init" });
		CamTool.main(new String[] { "sort" });
		CamTool.main(new String[] { "showGroups", "-n", Constants.DEFAULT_SORTING_NAME });
		CamTool.main(new String[] { "render", "-n", Constants.DEFAULT_SORTING_NAME , "-r", "pp3minmax,Rank,3,3"});
	}

	@Test
	public void testRenderWithRenderScript() throws IOException {
		String firstFileTime = null;
		for (int i = 1; i <= 3; i++) {
			String file = "stack_0" + i + ".jpg";
			String path = "stack/" + file;
			Files.copy(TestFileHelper.getTestResource(path), Paths.get(file));
			if (firstFileTime == null) {
				firstFileTime = FileUtils.getTimestamp(Paths.get(file).toRealPath());
			}
		}
		
		CamTool.main(new String[] { "init" });
		CamTool.main(new String[] { "sort", "-s" });
		
		Files.copy(TestFileHelper.getTestResource("scripts/dri.js"), Paths.get(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve(firstFileTime + "_multi").resolve(Constants.FILE_NAME_RENDERSCRIPT));
		
		CamTool.main(new String[] { "render", "-n", Constants.DEFAULT_SORTING_NAME });

		assertEquals(1, Files.list(Paths.get(Constants.FOLDER_RESULTS).resolve(Constants.DEFAULT_SORTING_NAME)).count());
	}

	@Test
	public void testRenderWithRenderSubstitute() throws IOException {
		for (int i = 6; i < 9; i++) {
			String file = "series_0" + i + ".JPG";
			String path = "series/" + file;
			Files.copy(TestFileHelper.getTestResource(path), Paths.get(file));
		}
		
		CamTool.main(new String[] { "init" });
		CamTool.main(new String[] { "sort", "-s" });
		
		Path path = Paths.get(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).resolve("20170915165451000_multi").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE);
		Files.write(path, "20170915165451000_series_06.JPG".getBytes());
		
		CamTool.main(new String[] { "render", "-n", Constants.DEFAULT_SORTING_NAME });

		assertTrue(Files.exists(Paths.get(Constants.FOLDER_RESULTS).resolve(Constants.DEFAULT_SORTING_NAME).resolve("20170915165451000_multi.JPG")));
		assertEquals(2, Files.list(Paths.get(Constants.FOLDER_RESULTS).resolve(Constants.DEFAULT_SORTING_NAME)).count());
	}
}
