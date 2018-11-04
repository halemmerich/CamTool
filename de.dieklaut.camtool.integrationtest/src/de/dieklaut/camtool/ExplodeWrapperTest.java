package de.dieklaut.camtool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class ExplodeWrapperTest extends WorkingDirTest {
	
	@Test
	public void testExplodeWithAbsolutePaths() throws IOException {
		Context context = TestFileHelper.createComplexContext(Paths.get("").toAbsolutePath());

		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("group/file1.arw"), 1000);
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("group/file2.arw"), 2000);
		
		Path sorting = Paths.get(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);

		CamTool.workingDir = sorting.toAbsolutePath();
		CamTool.main(new String [] {"Explode", sorting.resolve("group").toAbsolutePath().toString()});

		assertTrue(Files.exists(sorting.resolve(file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve(file2.getFileName())));
		assertFalse(Files.exists(file1));
		assertFalse(Files.exists(file2));
	}
	
	@Test
	public void testExplodeWithRelativePaths() throws IOException {
		Context context = TestFileHelper.createComplexContext(Paths.get("").toAbsolutePath());

		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("group/file1.arw"), 1000);
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("group/file2.arw"), 2000);
		
		Path sorting = Paths.get(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		
		CamTool.workingDir = sorting.toAbsolutePath();
		CamTool.main(new String [] {"Explode", CamTool.workingDir.relativize(sorting.resolve("group").toAbsolutePath()).toString()});

		assertTrue(Files.exists(sorting.resolve(file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve(file2.getFileName())));
		assertFalse(Files.exists(file1));
		assertFalse(Files.exists(file2));
	}
}
