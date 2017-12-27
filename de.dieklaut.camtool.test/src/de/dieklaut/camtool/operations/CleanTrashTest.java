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
import de.dieklaut.camtool.FileOperationException;
import de.dieklaut.camtool.TestFileHelper;
import de.dieklaut.camtool.util.FileUtils;

public class CleanTrashTest extends FileBasedTest {

	private static final DefaultSorter SORTER = new DefaultSorter();
	private static final String TEST = "test";

	@Test
	public void testPerform() throws IOException, FileOperationException {		
		Context context = Context.create(getTestFolder());
		
		Path source = TestFileHelper.getTestResource("A7II.ARW");
		Files.copy(source, getTestFolder().resolve("file.arw"));
		String timestamp = FileUtils.getTimestamp(FileUtils.getCreationDate(source));
		
		source = TestFileHelper.getTestResource("NEX5R.ARW");
		Files.copy(source, getTestFolder().resolve("file2.arw"));
		String timestamp2 = FileUtils.getTimestamp(FileUtils.getCreationDate(source));
		
		new Init().perform(context);
		Sort sort = new Sort(SORTER);
		sort.setName(TEST);
		sort.perform(context);
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(TEST);
		
		source = TestFileHelper.getTestResource("neutral_deleted.pp3");
		Files.copy(source, sorting.resolve(timestamp + "_file.pp3"));
		assertTrue(Files.exists(sorting.resolve(timestamp2 + "_file2.arw")));
		assertTrue(Files.exists(sorting.resolve(timestamp + "_file.arw")));
		assertTrue(Files.exists(sorting.resolve(timestamp + "_file.pp3")));
		
		CleanTrash cleanTrash = new CleanTrash(SORTER);
		cleanTrash.setName(TEST);
		cleanTrash.perform(context);
		
		assertTrue(Files.exists(sorting));
		assertTrue(!Files.exists(sorting.resolve(timestamp + "_file.arw")));
		assertTrue(Files.exists(sorting.resolve(timestamp2 + "_file2.arw")));
		assertTrue(!Files.exists(sorting.resolve(timestamp + "_file.pp3")));
	}
}
