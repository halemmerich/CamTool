package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.FileOperationException;
import de.dieklaut.camtool.TestFileHelper;
import de.dieklaut.camtool.renderjob.DummyRawRenderJob;
import de.dieklaut.camtool.renderjob.RenderJob;
import de.dieklaut.camtool.renderjob.RenderJobFactory;
import de.dieklaut.camtool.renderjob.RenderJobFactoryProvider;
import de.dieklaut.camtool.util.FileUtils;

public class RenderTest extends FileBasedTest {

	private static final String TEST = "test";

	@Test
	public void testPerform() throws IOException, FileOperationException {
		RenderJobFactory.setFactoryInstance(new RenderJobFactoryProvider() {
			@Override
			public RenderJob forFile(Path mainFile, Path... helperFiles) {
				return new DummyRawRenderJob(mainFile);
			}
		});
		
		Context context = Context.create(getTestFolder());
		
		Path source = TestFileHelper.getTestResource("A7II.ARW");
		Files.copy(source, getTestFolder().resolve("file.arw"));
		
		new Init().perform(context);
		Sort sort = new Sort();
		sort.setName(TEST);
		sort.perform(context);
		
		Render render = new Render();
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
