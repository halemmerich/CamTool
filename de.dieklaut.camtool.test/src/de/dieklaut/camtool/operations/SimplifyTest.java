package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.DefaultSorter;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.TestFileHelper;

public class SimplifyTest extends FileBasedTest {
	
	private static final Sorter SORTER = new DefaultSorter();
	
	@Test
	public void testSimplify() throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());


		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("file1.JPG"), 2000L);
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("multi", "file2.JPG"), 3000L);

		assertTrue(Files.exists(file1));
		assertTrue(Files.exists(file2));
		
		Simplify simplify = new Simplify(SORTER);
		simplify.perform(context);

		assertTrue(Files.exists(file1));
		assertFalse(Files.exists(file2));
		assertTrue(Files.exists(file1.getParent().resolve(file2.getFileName())));
	}
}
