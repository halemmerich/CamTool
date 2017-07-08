package de.dieklaut.camtool;

import static org.junit.Assert.assertNotNull;

import java.nio.file.Paths;

import org.junit.Test;

public class ImageFileTest {
	@Test
	public void getCreationDateTestNoExif() {
		assertNotNull(new ImageFile(Paths.get("res", "noexif.png")).getCreationDate());
	}
	@Test
	public void getCreationDateTestNoExifArw() {
		assertNotNull(new ImageFile(Paths.get("res", "A7II.ARW")).getCreationDate());
		assertNotNull(new ImageFile(Paths.get("res", "NEX5R.ARW")).getCreationDate());
	}
}
