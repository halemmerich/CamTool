package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;
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
import de.dieklaut.camtool.RenderSubstituteModifier;
import de.dieklaut.camtool.SingleGroup;
import de.dieklaut.camtool.TestFileHelper;
import de.dieklaut.camtool.renderjob.RenderJobFactory;
import de.dieklaut.camtool.util.FileUtils;

public class RenderSubstituteModifierTest extends FileBasedTest {
	
	@Test
	public void testRenderWithSubstitute() throws IOException {
		Path source = TestFileHelper.getTestResource("A7II.ARW");
		Path file1 = getTestFolder().resolve("file1.arw");
		Files.copy(source, file1);
		Path file2 = getTestFolder().resolve("file2.arw");
		Files.copy(source, file2);
		
		Path renderscript = getTestFolder().resolve("test.camtool_rendersubstitute");
		Files.write(renderscript, "file1.arw".getBytes());
		
		Collection<Group> groups = new HashSet<>();
		SingleGroup singleGroup = new SingleGroup(Arrays.asList(new Path [] {file2}));
		groups.add(singleGroup);
		Collection<Group> multigroups = new HashSet<>();
		SingleGroup subGroup = new SingleGroup(Arrays.asList(new Path [] {file1}));
		multigroups.add(subGroup);
		MultiGroup multiGroup = new MultiGroup(multigroups);
		groups.add(multiGroup);

		RenderJobFactory.useRawtherapee = false;
		RenderSubstituteModifier renderModifier = new RenderSubstituteModifier(renderscript, null);
		multiGroup.setRenderModifier(renderModifier);

		
		Path dest = Files.createTempDirectory(Constants.TEMP_FOLDER_PREFIX);
		
		renderModifier.getRenderJob(Collections.emptySet()).store(dest, Collections.emptySet());

		assertTrue(Files.exists(dest.resolve("file1.jpg")));
		assertEquals(Files.list(dest).count(), 1);
		FileUtils.deleteRecursive(dest, true);
		
	}
	
	@Test
	public void testRenderWithSubstituteIncorrectContent() throws IOException {
		Path source = TestFileHelper.getTestResource("A7II.ARW");
		Path file1 = getTestFolder().resolve("file1.arw");
		Files.copy(source, file1);
		Path file2 = getTestFolder().resolve("file2.arw");
		Files.copy(source, file2);
		
		Path renderscript = getTestFolder().resolve("test.camtool_rendersubstitute");
		Files.write(renderscript, "nothere.arw".getBytes());
		
		Collection<Group> groups = new HashSet<>();
		SingleGroup singleGroup = new SingleGroup(Arrays.asList(new Path [] {file2}));
		groups.add(singleGroup);
		Collection<Group> multigroups = new HashSet<>();
		SingleGroup subGroup = new SingleGroup(Arrays.asList(new Path [] {file1}));
		multigroups.add(subGroup);
		MultiGroup multiGroup = new MultiGroup(multigroups);
		groups.add(multiGroup);

		RenderJobFactory.useRawtherapee = false;
		RenderSubstituteModifier renderModifier = new RenderSubstituteModifier(renderscript, null);
		multiGroup.setRenderModifier(renderModifier);

		
		Path dest = Files.createTempDirectory(Constants.TEMP_FOLDER_PREFIX);
		
		renderModifier.getRenderJob(Collections.emptySet()).store(dest, Collections.emptySet());

		assertEquals(0, Files.list(dest).count());
		FileUtils.deleteRecursive(dest, true);
		
	}
	
	@Test
	public void testRenderWithSubstituteRegex() throws IOException {
		Path source = TestFileHelper.getTestResource("A7II.ARW");
		Path file1 = getTestFolder().resolve("file1.arw");
		Files.copy(source, file1);
		Path file2 = getTestFolder().resolve("file2.arw");
		Files.copy(source, file2);
		source = TestFileHelper.getTestResource("neutral.pp3");
		Path pp3 = getTestFolder().resolve("file1.pp3");
		Files.copy(source, pp3);
		
		Path renderscript = getTestFolder().resolve("test.camtool_rendersubstitute");
		Files.write(renderscript, "file1.*".getBytes());
		
		Collection<Group> groups = new HashSet<>();
		Collection<Group> multigroups = new HashSet<>();
		SingleGroup singleGroup = new SingleGroup(Arrays.asList(new Path [] {file2}));
		groups.add(singleGroup);
		SingleGroup subGroup = new SingleGroup(Arrays.asList(new Path [] {file1 , pp3}));
		multigroups.add(subGroup);
		MultiGroup multiGroup = new MultiGroup(multigroups);
		groups.add(multiGroup);

		RenderJobFactory.useRawtherapee = false;
		RenderSubstituteModifier renderModifier = new RenderSubstituteModifier(renderscript, null);
		multiGroup.setRenderModifier(renderModifier);

		
		Path dest = Files.createTempDirectory(Constants.TEMP_FOLDER_PREFIX);
		
		renderModifier.getRenderJob(Collections.emptySet()).store(dest, Collections.emptySet());

		assertTrue(Files.exists(dest.resolve("file1.jpg")));
		assertEquals(Files.list(dest).count(), 1);
		FileUtils.deleteRecursive(dest, true);
		
	}
}
