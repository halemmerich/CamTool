package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.DefaultSorter;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.UserInterface;
import de.dieklaut.camtool.util.FileUtils;

public class ShowGroupsTest extends FileBasedTest{
	private static final String TEST = "test";
	
	private int showCalledCounter;
	
	@Before
	public void setUp() {
		showCalledCounter = 0;
	}
	
	@Test
	public void testPerform() throws IOException {
		Context context = Context.create(getTestFolder());
		
		Files.createFile(getTestFolder().resolve("file1.ARW"));
		Files.createFile(getTestFolder().resolve("file1.JPG"));
		Files.createFile(getTestFolder().resolve("file2.ARW"));
		Files.createFile(getTestFolder().resolve("file2.JPG"));
		Files.createFile(getTestFolder().resolve("file3.ARW"));
		Files.createFile(getTestFolder().resolve("file3.JPG"));
		Files.createFile(getTestFolder().resolve("file4.ARW"));
		Files.createFile(getTestFolder().resolve("file4.JPG"));

		Sorter sorter = new DefaultSorter();
		
		new Init().perform(context);
		Sort sort = new Sort(sorter);
		sort.setName(TEST);
		sort.perform(context);
		
		String timestamp = FileUtils.getTimestampPortion(Files.list(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(TEST)).filter(new Predicate<Path>() {

			@Override
			public boolean test(Path t) {
				return t.getFileName().toString().contains("file2");
			}
		}).findFirst().get().toString()) + "";
		
		Path collection = Files.createFile(getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(TEST).resolve("file.camtool_collection"));
		Files.write(collection, (timestamp + "_file2\n" + timestamp + "_file3\n" + timestamp + "_file4\n").getBytes());
		
		ShowGroups showGroups = new ShowGroups(sorter, new UserInterface() {

			@Override
			public void show(String text) {
				Logger.log(text, Level.DEBUG);
				showCalled(text);
			}
			
		});
		showGroups.setSortingName(TEST);
		
		showGroups.perform(context);
	
		
		assertEquals(1, showCalledCounter);
	}

	protected void showCalled(String text) {
		showCalledCounter++;
	}
}
