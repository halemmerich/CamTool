package de.dieklaut.camtool;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ImageFileTest {
	@Test
	public void getCreationDateTestNoExif() throws FileOperationException {
		assertNotNull(new ImageFile(TestFileHelper.getTestResource("noexif.png")).getCreationDate());
	}
	@Test
	public void getCreationDateTestNoExifArw() throws FileOperationException {
		assertNotNull(new ImageFile(TestFileHelper.getTestResource("A7II.ARW")).getCreationDate());
		assertNotNull(new ImageFile(TestFileHelper.getTestResource("NEX5R.ARW")).getCreationDate());
	}
}
