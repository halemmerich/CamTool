package de.dieklaut.camtool;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

public class SortingHelperTest extends FileBasedTest{
	@Test
	public void testCombineSeries() throws IOException {
		Collection<Group> groups = new LinkedList<>();
		
		Path source = TestFileHelper.getTestResource("series/series_09.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("series_09.ARW"))));
		
		source = TestFileHelper.getTestResource("series/series_07.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("series_07.ARW"))));

		source = TestFileHelper.getTestResource("series/series_06.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("series_06.ARW"))));
		
		source = TestFileHelper.getTestResource("series/series_08.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("series_08.ARW"))));
				
		SortingHelper.combineSeries(groups, 2);
		
		assertEquals(1, groups.size());
		assertEquals(4, ((MultiGroup)groups.stream().findFirst().get()).getGroups().size());
		
	}
	
	@Test
	public void testCombineMultipleSeries() throws IOException {
		Collection<Group> groups = new LinkedList<>();
		
		Path source = TestFileHelper.getTestResource("series/series_09.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("series_09.ARW"))));
		
		source = TestFileHelper.getTestResource("series/series_07.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("series_07.ARW"))));

		source = TestFileHelper.getTestResource("series/series_06.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("series_06.ARW"))));
		
		source = TestFileHelper.getTestResource("series/series_08.ARW");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("series_08.ARW"))));
		
		source = TestFileHelper.getTestResource("stack/stack_01.jpg");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("stack_01.jpg"))));
		
		source = TestFileHelper.getTestResource("stack/stack_02.jpg");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("stack_02.jpg"))));
		
		source = TestFileHelper.getTestResource("stack/stack_03.jpg");
		groups.add(new SingleGroup(Files.copy(source, getTestFolder().resolve("stack_03.jpg"))));
				
		SortingHelper.combineSeries(groups, 2);
		
		assertEquals(2, groups.size());
		
	}
}
