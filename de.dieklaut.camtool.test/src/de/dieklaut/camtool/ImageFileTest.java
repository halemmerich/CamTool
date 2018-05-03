package de.dieklaut.camtool;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ImageFileTest {
	@Test
	public void getCreationDateTestNoExif() {
		assertNotNull(new ImageFile(TestFileHelper.getTestResource("noexif.png")).getCreationDate());
	}
	@Test
	public void getCreationDateTestNoExifArw() {
		assertNotNull(new ImageFile(TestFileHelper.getTestResource("A7II.ARW")).getCreationDate());
		assertNotNull(new ImageFile(TestFileHelper.getTestResource("NEX5R.ARW")).getCreationDate());
	}
}
