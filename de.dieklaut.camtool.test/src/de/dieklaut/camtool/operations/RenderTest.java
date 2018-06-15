package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
}
