package de.dieklaut.camtool;

import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class RenderScriptMultiRenderJobTest extends FileBasedTest{
	private static final String DOESNT_EXIST_NULL = "doesntExist.null";

	@Test
	public void test() {
		
		List<Path> arguments = new LinkedList<>();
		
		arguments.add(getTestFolder().resolve(DOESNT_EXIST_NULL));
		
		RenderScriptMultiRenderJob.execRenderScript(TestFileHelper.getTestResource("scripts/createFile.js"), arguments);

		assertTrue(Files.exists(getTestFolder().resolve(DOESNT_EXIST_NULL)));
	}
}
