package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import de.dieklaut.camtool.util.FileUtils;

public class CamToolTest extends FileBasedTest {

	private static final String TEST = "test";

	@Before
	public void setUp() throws InterruptedException, IOException {
		if (System.getenv("NEEDS_WORKING_DIR") == null) {
			System.err.println(
					"DO NOT EXECUTE THIS TEST WITHOUT SETTING A DEDICATED WORKING DIRECTORY!\nIT WILL DESTROY YOUR STUFF!\n\n!!!!ALL CONTENTS OF THE WORKING DIRECTORY WILL BE DELETED!!!!");
			System.exit(1);
		}
		Files.list(Paths.get("")).forEach(currentPath -> FileUtils.deleteRecursive(currentPath, false));
	}

	@Test
	public void callNoArgs() {
		CamTool.main(new String[] {});
	}

	@Test
	public void callHelp() {
		CamTool.main(new String[] { "-h" });
	}

	@Test
	public void callInitNoArgs() {
		CamTool.main(new String[] { "init" });
	}

	@Test
	public void callSortNoArgs() {
		callInitNoArgs();
		CamTool.main(new String[] { "sort" });
	}

	@Test
	public void callSortHelp() {
		callInitNoArgs();
		CamTool.main(new String[] { "sort", "-h" });
	}

	@Test
	public void callSortWithName() {
		CamTool.main(new String[] { "init" });
		CamTool.main(new String[] { "sort", "-n", TEST });
	}

	@Test
	public void callSortWithDetectSeries() {
		callInitNoArgs();
		CamTool.main(new String[] { "sort", "-s" });
	}

	@Test
	public void callSortWithMoveAllGroups() {
		callInitNoArgs();
		CamTool.main(new String[] { "sort", "-a" });
	}

	@Test
	public void callSortWithMoveCollections() {
		callInitNoArgs();
		CamTool.main(new String[] { "sort", "-c" });
	}

	@Test
	public void callCleanTrashNoArgs() {
		callSortNoArgs();
		CamTool.main(new String[] { "cleantrash" });
	}

	@Test
	public void callCleanTrashHelp() {
		callInitNoArgs();
		CamTool.main(new String[] { "cleantrash", "-h" });
	}

	@Test
	public void callRenderNoArgs() {
		callSortNoArgs();
		CamTool.main(new String[] { "render" });
	}

	@Test
	public void callRenderHelp() {
		callInitNoArgs();
		CamTool.main(new String[] { "render", "-h" });
	}

	@Test
	public void callRenderWithName() {
		callSortWithName();
		CamTool.main(new String[] { "render", "-n", TEST });
	}

	@Test
	public void callExportNoArgs() {
		callRenderNoArgs();
		CamTool.main(new String[] { "export" });
	}

	@Test
	public void callExportHelp() {
		callRenderNoArgs();
		CamTool.main(new String[] { "export", "-h" });
	}

	@Test
	public void callExportWithName() {
		callRenderWithName();
		CamTool.main(new String[] { "export", "-n", TEST });
	}

	@Test
	public void callUpdateUnusedNoArgs() {
		callSortNoArgs();
		CamTool.main(new String[] { "updateunused" });
	}

	@Test
	public void callDeleteUnusedNoArgs() {
		callSortNoArgs();
		CamTool.main(new String[] { "deleteunused" });
	}

	@Test
	public void callDeleteUnusedNoAutoNoUnused() {
		callSortNoArgs();
		CamTool.main(new String[] { "deleteunused", "-n" });
	}

	@Test
	public void callDeleteUnusedNoAuto() {
		callUpdateUnusedNoArgs();
		CamTool.main(new String[] { "deleteunused", "-n" });
	}
}
