package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.DefaultSorter;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.TestFileHelper;
import de.dieklaut.camtool.UserInterface;

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
		
		Path source = TestFileHelper.getTestResource("A7II.ARW");
		Files.copy(source, getTestFolder().resolve("file.arw"));
		
		new Init().perform(context);
		Sorter sorter = new DefaultSorter();
		Sort sort = new Sort(sorter);
		sort.setName(TEST);
		sort.perform(context);
		
		ShowGroups showGroups = new ShowGroups(sorter, new UserInterface() {

			@Override
			public void show(String text) {
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
