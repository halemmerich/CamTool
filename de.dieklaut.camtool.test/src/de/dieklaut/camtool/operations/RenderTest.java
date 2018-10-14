package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.DefaultSorter;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.TestFileHelper;
import de.dieklaut.camtool.renderjob.RenderJobFactory;
import de.dieklaut.camtool.util.FileUtils;

public class RenderTest extends FileBasedTest {

	private static final String TEST = "test";

	@Test
	public void testPerform() throws IOException {
		Context context = Context.create(getTestFolder());
		
		Path source = TestFileHelper.getTestResource("A7II.ARW");
		Files.copy(source, getTestFolder().resolve("file.arw"));
		
		new Init().perform(context);
		Sorter sorter = new DefaultSorter();
		Sort sort = new Sort(sorter);
		sort.setName(TEST);
		sort.perform(context);
		
		RenderJobFactory.useRawtherapee = false;
		
		Render render = new Render(sorter);
		render.setSortingName(TEST);
		
		render.perform(context);
		
		String timestamp = FileUtils.getTimestamp(FileUtils.getCreationDate(source));
		
		Path results = getTestFolder().resolve(Constants.FOLDER_RESULTS);
		assertTrue(Files.exists(results));
		assertTrue(Files.exists(results.resolve(TEST).resolve(Constants.RENDER_TYPE_FULL)));
		assertTrue(Files.exists(results.resolve(TEST).resolve(Constants.RENDER_TYPE_FULL).resolve(timestamp + "_file.jpg")));
		assertTrue(Files.exists(results.resolve(TEST).resolve(Constants.RENDER_TYPE_MEDIUM)));
		assertTrue(Files.exists(results.resolve(TEST).resolve(Constants.RENDER_TYPE_MEDIUM).resolve(timestamp + "_file.jpg")));
		assertTrue(Files.exists(results.resolve(TEST).resolve(Constants.RENDER_TYPE_SMALL)));
		assertTrue(Files.exists(results.resolve(TEST).resolve(Constants.RENDER_TYPE_SMALL).resolve(timestamp + "_file.jpg")));
	}

	@Test
	public void testPerformOneGroup() throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("group/file1.ARW"), TestFileHelper.getTestResource("A7II.ARW"));
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("file2.ARW"), TestFileHelper.getTestResource("A7II.ARW"));

		Sorter sorter = new DefaultSorter();		
		RenderJobFactory.useRawtherapee = false;
		
		Render render = new Render(sorter);
		render.setSortingName(Constants.DEFAULT_SORTING_NAME);
		render.setNameOfGroup("group");
		render.perform(context);
		
		Path results = getTestFolder().resolve(Constants.FOLDER_RESULTS);
		assertTrue(Files.exists(results));
		assertTrue(Files.exists(results.resolve(Constants.DEFAULT_SORTING_NAME).resolve(Constants.RENDER_TYPE_FULL)));
		assertTrue(Files.exists(results.resolve(Constants.DEFAULT_SORTING_NAME).resolve(Constants.RENDER_TYPE_FULL).resolve(FileUtils.getTimestamp(file1) + "_file1.jpg")));
		assertTrue(Files.exists(results.resolve(Constants.DEFAULT_SORTING_NAME).resolve(Constants.RENDER_TYPE_MEDIUM)));
		assertTrue(Files.exists(results.resolve(Constants.DEFAULT_SORTING_NAME).resolve(Constants.RENDER_TYPE_MEDIUM).resolve(FileUtils.getTimestamp(file1) + "_file1.jpg")));
		assertTrue(Files.exists(results.resolve(Constants.DEFAULT_SORTING_NAME).resolve(Constants.RENDER_TYPE_SMALL)));
		assertTrue(Files.exists(results.resolve(Constants.DEFAULT_SORTING_NAME).resolve(Constants.RENDER_TYPE_SMALL).resolve(FileUtils.getTimestamp(file1) + "_file1.jpg")));
		assertFalse(Files.exists(results.resolve(Constants.DEFAULT_SORTING_NAME).resolve(Constants.RENDER_TYPE_FULL).resolve(FileUtils.getTimestamp(file2) + "_file2.jpg")));
		assertFalse(Files.exists(results.resolve(Constants.DEFAULT_SORTING_NAME).resolve(Constants.RENDER_TYPE_MEDIUM).resolve(FileUtils.getTimestamp(file2) + "_file2.jpg")));
		assertFalse(Files.exists(results.resolve(Constants.DEFAULT_SORTING_NAME).resolve(Constants.RENDER_TYPE_SMALL).resolve(FileUtils.getTimestamp(file2) + "_file2.jpg")));
	}
}
