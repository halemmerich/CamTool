package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.DefaultSorter;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.TestFileHelper;
import de.dieklaut.camtool.util.FileUtils;

public class ExportTest extends FileBasedTest {

	private static final String TEST = "test";
	private String timestamp = null;
	private Context context = null;
	private DefaultSorter sorter;

	@Before
	public void setUp() throws IOException {		
		sorter = new DefaultSorter();
		sorter.useRawTherapee = false;
		
		context  = Context.create(getTestFolder());
		
		Path source = TestFileHelper.getTestResource("A7II.ARW");
		Files.copy(source, getTestFolder().resolve("file.arw"));
		
		new Init().perform(context);
		Sort sort = new Sort(sorter);
		sort.setName(TEST);
		sort.perform(context);
		
		Render render = new Render(sorter);
		render.setSortingName(TEST);
		
		render.perform(context);

		timestamp = FileUtils.getTimestamp(FileUtils.getCreationDate(source));
	}
	
	@Test
	public void testPerformNoDestination() throws IOException {
		Export export = new Export();
		export.setName(TEST);
		export.setType(ExportType.MEDIUM);
		export.perform(context);
		
		Path results = getTestFolder().resolve(Constants.DEFAULT_EXPORT_NAME);
		assertTrue(Files.exists(results));
		Path exportDestination = results.resolve(ExportType.MEDIUM.name().toLowerCase()).resolve(TEST);
		assertTrue(Files.exists(exportDestination));
		assertTrue(Files.exists(exportDestination.resolve(timestamp + "_file.jpg")));
	}
	
	@Test
	public void testPerformWithDestination() throws IOException {
		Path tempDest = Files.createTempDirectory("toBeDeleted");
		tempDest.toFile().deleteOnExit();
		
		Export export = new Export();
		export.setName(TEST);
		export.setType(ExportType.MEDIUM);
		export.setDestination(tempDest);
		export.perform(context);
		
		Path exportDestination = tempDest;
		assertTrue(Files.exists(exportDestination));
		assertTrue(Files.exists(exportDestination.resolve(timestamp + "_file.jpg")));
	}
}
