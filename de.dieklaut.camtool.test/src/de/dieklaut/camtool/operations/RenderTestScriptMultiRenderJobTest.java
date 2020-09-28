package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.MultiGroup;
import de.dieklaut.camtool.SingleGroup;
import de.dieklaut.camtool.TestFileHelper;
import de.dieklaut.camtool.renderjob.RenderJobFactory;
import de.dieklaut.camtool.renderjob.RenderJavaScriptScriptMultiRenderJob;
import de.dieklaut.camtool.util.FileUtils;

public class RenderTestScriptMultiRenderJobTest extends FileBasedTest {
	
	@Test
	public void testRenderWithScript() throws IOException {
		Path source = TestFileHelper.getTestResource("A7II.ARW");
		Path file1 = getTestFolder().resolve("file1.arw");
		Files.copy(source, file1);
		Path file2 = getTestFolder().resolve("file2.arw");
		Files.copy(source, file2);
		
		Path script = TestFileHelper.getTestResource("scripts/createSummaryFile.js");
		Path renderscript = getTestFolder().resolve("test.camtool_renderscript");
		Files.copy(script, renderscript);
		
		Collection<Group> groups = new HashSet<>();
		SingleGroup singleGroup = new SingleGroup(Arrays.asList(new Path [] {file2}));
		groups.add(singleGroup);
		Collection<Group> multigroups = new HashSet<>();
		SingleGroup subGroup = new SingleGroup(Arrays.asList(new Path [] {file1}));
		multigroups.add(subGroup);
		MultiGroup multiGroup = new MultiGroup(multigroups);
		groups.add(multiGroup);

		RenderJobFactory.useRawtherapee = false;
		
		RenderJavaScriptScriptMultiRenderJob renderJob = new RenderJavaScriptScriptMultiRenderJob(renderscript, multiGroup);
		
		Path dest = Files.createTempDirectory(Constants.TEMP_FOLDER_PREFIX);
		
		renderJob.store(dest, Collections.emptySet());
		
		assertTrue(Files.exists(dest.resolve("summary.txt")));
		FileUtils.deleteRecursive(dest, true);
		
	}
}
