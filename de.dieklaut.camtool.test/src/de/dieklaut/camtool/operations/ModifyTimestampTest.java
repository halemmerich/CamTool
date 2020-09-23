package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.TestFileHelper;
import de.dieklaut.camtool.util.FileUtils;

public class ModifyTimestampTest extends FileBasedTest {

	@Before
	public void setUp() throws IOException {
	}
	
	@Test
	public void testSimpleShift() throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("file1.arw"), TestFileHelper.getTestResource("A7II.ARW"));
		Path timeline_file1 = FileUtils.resolve(file1);
		Path orig_file1 = FileUtils.resolve(timeline_file1);
		
		ModifyTimestamp mts = new ModifyTimestamp();
		mts.setRegex(".*");
		mts.setDifference(1000*60*60);
		
		String expectedRename = "20170708101350000_file1.arw";
		
		mts.perform(context);

		Path renamed_timeline = getTestFolder().resolve(Constants.FOLDER_TIMELINE).resolve(expectedRename);
		
		//file in sorting remains unchanged
		assertTrue(Files.exists(file1));
		
		//file in timeline must be renamed with the same target
		assertEquals(renamed_timeline, Files.list(getTestFolder().resolve(Constants.FOLDER_TIMELINE)).findFirst().get());
		assertEquals(orig_file1, FileUtils.resolve(renamed_timeline));
		assertEquals(renamed_timeline, FileUtils.resolve(file1));
	}
	
	@Test
	public void testSimpleShiftGivenStamp() throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("file1.arw"), TestFileHelper.getTestResource("A7II.ARW"));
		Path timeline_file1 = FileUtils.resolve(file1);
		Path orig_file1 = FileUtils.resolve(timeline_file1);
		
		ModifyTimestamp mts = new ModifyTimestamp();
		mts.setRegex(".*");
		mts.setTimestamp("20170708101350000");
		
		String expectedRename = "20170708101350000_file1.arw";
		
		mts.perform(context);

		Path renamed_timeline = getTestFolder().resolve(Constants.FOLDER_TIMELINE).resolve(expectedRename);
		
		//file in sorting remains unchanged
		assertTrue(Files.exists(file1));
		
		//file in timeline must be renamed with the same target
		assertEquals(renamed_timeline, Files.list(getTestFolder().resolve(Constants.FOLDER_TIMELINE)).findFirst().get());
		assertEquals(orig_file1, FileUtils.resolve(renamed_timeline));
		assertEquals(renamed_timeline, FileUtils.resolve(file1));
	}
}
