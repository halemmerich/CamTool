package de.dieklaut.camtool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.dieklaut.camtool.util.FileUtils;

public class MoveWrapperTest extends WorkingDirTest {
	
	@Test
	public void testMoveWithCombine() throws IOException {
		Context context = TestFileHelper.createComplexContext(Paths.get("").toAbsolutePath());

		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("file1.arw"), 1000);
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("file2.arw"), 2000);
		Path file3 = TestFileHelper.addFileToSorting(context, Paths.get("file3.arw"), 3000);
		Path file4 = TestFileHelper.addFileToSorting(context, Paths.get("group/file4.arw"), 4000);
		
		Path sorting = Paths.get(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).toAbsolutePath();

		CamTool.workingDir = sorting;
		CamTool.main(new String [] {"move", "-c", sorting.resolve(file1.getFileName()).toString(), sorting.resolve(file2.getFileName()).toString(), sorting.resolve(file4.getParent().getFileName()).toString()});

		assertTrue(Files.exists(sorting.resolve(FileUtils.getTimestampPortion(file1.getFileName().toString()) + "_multi").resolve(file1.getFileName())));
		assertTrue(Files.exists(sorting.resolve(FileUtils.getTimestampPortion(file1.getFileName().toString()) + "_multi").resolve(file2.getFileName())));
		assertTrue(Files.exists(sorting.resolve(FileUtils.getTimestampPortion(file1.getFileName().toString()) + "_multi").resolve("group").resolve(file4.getFileName())));
		assertTrue(Files.exists(sorting.resolve(file3.getFileName())));
	}
	
	@Test
	public void testMoveDownWithAbsolutePaths() throws IOException {
		Context context = TestFileHelper.createComplexContext(Paths.get("").toAbsolutePath());

		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("group/file1.arw"), 1000);
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("group/file2.arw"), 2000);
		
		Path sorting = Paths.get(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).toAbsolutePath();

		CamTool.workingDir = sorting;
		CamTool.main(new String [] {"move", sorting.resolve("group").resolve(file1.getFileName()).toString(), sorting.toString()});

		assertTrue(Files.exists(sorting.resolve(file1.getFileName().toString())));
		assertFalse(Files.exists(sorting.resolve(file2.getFileName().toString())));
		assertFalse(Files.exists(sorting.resolve("group/" + file1.getFileName().toString())));
		assertTrue(Files.exists(sorting.resolve("group/" + file2.getFileName().toString())));
	}
	
	@Test
	public void testMoveWithAbsolutePaths() throws IOException {
		Context context = TestFileHelper.createComplexContext(Paths.get("").toAbsolutePath());

		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("file1.arw"), 1000);
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("file2.arw"), 2000);
		
		Path sorting = Paths.get(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME).toAbsolutePath();
		
		String target = "target";

		CamTool.workingDir = sorting;
		CamTool.main(new String [] {"move", sorting.resolve(file1.getFileName()).toString(), sorting.resolve(target).toString()});

		assertFalse(Files.exists(sorting.resolve(file1.getFileName().toString())));
		assertTrue(Files.exists(sorting.resolve(file2.getFileName().toString())));
		assertTrue(Files.exists(sorting.resolve(target + "/" + file1.getFileName().toString())));
		assertFalse(Files.exists(sorting.resolve(target + "/" + file2.getFileName().toString())));
	}
	
	@Test
	public void testMoveWithRelativePaths() throws IOException {
		Context context = TestFileHelper.createComplexContext(Paths.get("").toAbsolutePath());

		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("file1.arw"), 1000);
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("file2.arw"), 2000);
		
		Path sorting = Paths.get(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);

		CamTool.workingDir = sorting.toAbsolutePath();
		CamTool.main(new String [] {"move", file1.getFileName().toString(), "group/"});

		assertFalse(Files.exists(sorting.resolve(file1.getFileName().toString())));
		assertTrue(Files.exists(sorting.resolve(file2.getFileName().toString())));
		assertTrue(Files.exists(sorting.resolve("group/" + file1.getFileName().toString())));
		assertFalse(Files.exists(sorting.resolve("group/" + file2.getFileName().toString())));
	}
	
	@Test
	public void testMoveDownWithRelativePaths() throws IOException {
		Context context = TestFileHelper.createComplexContext(Paths.get("").toAbsolutePath());

		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("group/file1.arw"), 1000);
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("group/file2.arw"), 2000);
		
		Path sorting = Paths.get(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);

		CamTool.workingDir = sorting.toAbsolutePath();
		CamTool.main(new String [] {"move", "group/" + file1.getFileName().toString(), "."});

		assertTrue(Files.exists(sorting.resolve(file1.getFileName().toString())));
		assertFalse(Files.exists(sorting.resolve(file2.getFileName().toString())));
		assertFalse(Files.exists(sorting.resolve("group/" + file1.getFileName().toString())));
		assertTrue(Files.exists(sorting.resolve("group/" + file2.getFileName().toString())));
	}
}
