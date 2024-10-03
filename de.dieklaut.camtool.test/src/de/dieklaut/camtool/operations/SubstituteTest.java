package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.TestFileHelper;

public class SubstituteTest extends FileBasedTest {
	
	@Test
	public void testSubstituteSingleFile () throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("group/file1.arw"), TestFileHelper.getTestResource("A7II.ARW"));
		
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		
		Substitute sub = new Substitute();
		sub.setNameOfGroup("group");
		sub.setSubstitutions(new String [] { file1.getFileName().toString() });
		sub.perform(context);

		assertTrue(Files.exists(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE))));
		assertEquals(file1.getFileName().toString(), new String(Files.readAllBytes(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE)))));
		assertFalse(Files.exists(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE_EXTERNAL))));
	}
	
	@Test
	public void testSubstituteMissingSingleFile () throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		
		Substitute sub = new Substitute();
		sub.setNameOfGroup("group");
		sub.setSubstitutions(new String [] {"file1.arw" });
		sub.perform(context);

		assertFalse(Files.exists(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE))));
		assertFalse(Files.exists(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE_EXTERNAL))));
	}
	
	@Test
	public void testSubstituteMultipleFiles () throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());

		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("group/file1.arw"), TestFileHelper.getTestResource("A7II.ARW"));
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("group/file2.arw"), TestFileHelper.getTestResource("A7II.ARW"));
		
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		
		Substitute sub = new Substitute();
		sub.setNameOfGroup("group");
		sub.setSubstitutions(new String [] { file1.getFileName().toString(), file2.getFileName().toString() });
		sub.perform(context);

		assertTrue(Files.exists(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE))));
		assertEquals(file1.getFileName().toString() + "\n" + file2.getFileName().toString(), new String(Files.readAllBytes(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE)))));
		assertFalse(Files.exists(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE_EXTERNAL))));
	}
	
	@Test
	public void testSubstituteSwitch () throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("group/file1.arw"), TestFileHelper.getTestResource("A7II.ARW"));
		
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		
		Substitute sub = new Substitute();
		sub.setNameOfGroup("group");
		sub.setSubstitutions(new String [] { file1.getFileName().toString() });
		sub.perform(context);

		assertTrue(Files.exists(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE))));
		assertEquals(file1.getFileName().toString(), new String(Files.readAllBytes(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE)))));
		assertFalse(Files.exists(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE_EXTERNAL))));

		sub = new Substitute();
		sub.setNameOfGroup("group");
		sub.setSwitch(true);
		sub.perform(context);

		assertTrue(Files.exists(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE))));
		assertEquals("", new String(Files.readAllBytes(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE)))));
		assertEquals(file1.getFileName().toString(), new String(Files.readAllBytes(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE_EXTERNAL)))));
		assertTrue(Files.exists(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE_EXTERNAL))));
		assertTrue(Files.exists(sorting.resolve(sorting.resolve(file1.getFileName().toString()))));
		
		sub = new Substitute();
		sub.setNameOfGroup("group");
		sub.setSwitch(true);
		sub.perform(context);

		assertTrue(Files.exists(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE))));
		assertEquals(file1.getFileName().toString(), new String(Files.readAllBytes(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE)))));
		assertFalse(Files.exists(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE_EXTERNAL))));
		assertFalse(Files.exists(sorting.resolve(sorting.resolve(file1.getFileName().toString()))));
	}
	
	@Test
	public void testSubstituteRemove () throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("group/file1.arw"), TestFileHelper.getTestResource("A7II.ARW"));
		
		Path sorting = getTestFolder().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		
		Substitute sub = new Substitute();
		sub.setNameOfGroup("group");
		sub.setSubstitutions(new String [] { file1.getFileName().toString() });
		sub.perform(context);

		assertTrue(Files.exists(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE))));
		assertEquals(file1.getFileName().toString(), new String(Files.readAllBytes(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE)))));
		assertFalse(Files.exists(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE_EXTERNAL))));

		sub = new Substitute();
		sub.setNameOfGroup("group");
		sub.setRemove(true);
		sub.perform(context);

		assertFalse(Files.exists(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE))));
		assertFalse(Files.exists(sorting.resolve(sorting.resolve("group").resolve(Constants.FILE_NAME_RENDERSUBSTITUTE_EXTERNAL))));
		assertFalse(Files.exists(sorting.resolve(sorting.resolve(file1.getFileName().toString()))));
		assertTrue(Files.exists(sorting.resolve(sorting.resolve("group").resolve(file1.getFileName().toString()))));
	}
}