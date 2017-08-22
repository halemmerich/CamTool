package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import de.dieklaut.camtool.util.FileUtils;

public class CamToolTest extends FileBasedTest{
	
	@Before
	public void setUp() throws InterruptedException, IOException {
		if (System.getenv("NEEDS_WORKING_DIR") == null) {
			System.err.println("DO NOT EXECUTE THIS TEST WITHOUT SETTING A DEDICATED WORKING DIRECTORY!\nIT WILL DESTROY YOUR STUFF!\n\n!!!!ALL CONTENTS OF THE WORKING DIRECTORY WILL BE DELETED!!!!");
			System.exit(1);
		}
		Files.list(Paths.get("")).forEach(currentPath -> {
			try {
				FileUtils.deleteRecursive(currentPath, false);
			} catch (FileOperationException e) {
				throw new IllegalStateException("Could not clear the working directory", e);
			}
		});
	}

	@Test
	public void testNoArgs() {
		CamTool.main(new String[] {});
	}

	@Test
	public void testHelp() {
		CamTool.main(new String[] {"-h"});
	}
	
	@Test
	public void testInitNoArgs() {
		CamTool.main(new String[] {"init"});
	}
	
	@Test
	public void testSortNoArgs() {
		CamTool.main(new String[] {"init"});
		CamTool.main(new String[] {"sort"});
	}
	
	@Test
	public void testSortHelp() {
		CamTool.main(new String[] {"init"});
		CamTool.main(new String[] {"sort", "-h"});
	}
}
