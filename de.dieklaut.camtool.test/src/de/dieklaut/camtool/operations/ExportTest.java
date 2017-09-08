package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
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

public class ExportTest extends FileBasedTest {

	private static final String TEST = "test";
	private String timestamp = null;
	private Context context = null;

	@Before
	public void setUp() throws IOException, FileOperationException {
		RenderJobFactory.getInstance();
		RenderJobFactory.setFactoryInstance(new RenderJobFactoryProvider() {
			@Override
			public RenderJob forFile(Path mainFile, Path... helperFiles) {
				return new DummyRawRenderJob(mainFile);
			}
		});
		
		context  = Context.create(getTestFolder());
		
		Path source = TestFileHelper.getTestResource("A7II.ARW");
		Files.copy(source, getTestFolder().resolve("file.arw"));
		
		new Init().perform(context);
		Sort sort = new Sort();
		sort.setName(TEST);
		sort.perform(context);
		
		Render render = new Render();
		render.setSortingName(TEST);
		
		render.perform(context);

		timestamp = FileUtils.getTimestamp(FileUtils.getCreationDate(source));
	}
	
	@Test
	public void testPerformNoDestination() throws IOException, FileOperationException {
		Export export = new Export();
		export.setName(TEST);
		export.setType(Constants.RENDER_TYPE_MEDIUM);
		export.perform(context);
		
		Path results = getTestFolder().resolve(Constants.DEFAULT_EXPORT_NAME);
		assertTrue(Files.exists(results));
		Path exportDestination = results.resolve(context.getName());
		assertTrue(Files.exists(exportDestination));
		assertTrue(Files.exists(exportDestination.resolve(timestamp + "_file.jpg")));
	}
	
	@Test
	public void testPerformWithDestination() throws IOException, FileOperationException {
		Path tempDest = Files.createTempDirectory("toBeDeleted");
		tempDest.toFile().deleteOnExit();
		
		Export export = new Export();
		export.setName(TEST);
		export.setType(Constants.RENDER_TYPE_MEDIUM);
		export.setDestination(tempDest);
		export.perform(context);
		
		Path exportDestination = tempDest.resolve(context.getName());
		assertTrue(Files.exists(exportDestination));
		assertTrue(Files.exists(exportDestination.resolve(timestamp + "_file.jpg")));
	}
}
