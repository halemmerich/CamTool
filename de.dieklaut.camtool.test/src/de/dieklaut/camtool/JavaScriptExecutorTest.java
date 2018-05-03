package de.dieklaut.camtool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

import org.junit.Test;

import de.dieklaut.camtool.renderjob.JavaScriptExecutor;

public class JavaScriptExecutorTest extends FileBasedTest{
	
	@Test
	public void test() throws IOException {
		Files.createFile(getTestFolder().resolve("testfile"));
		
		JavaScriptExecutor.execRenderScript(TestFileHelper.getTestResource("scripts/createSummaryFile.js"), getTestFolder(), getTestFolder(), Collections.emptyMap());

		assertTrue(Files.exists(getTestFolder().resolve("summary.txt")));
		assertEquals("testfile", new String(Files.readAllBytes(getTestFolder().resolve("summary.txt"))));
	}
}
