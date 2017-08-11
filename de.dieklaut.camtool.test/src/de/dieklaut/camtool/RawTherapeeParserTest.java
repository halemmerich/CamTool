package de.dieklaut.camtool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RawTherapeeParserTest {
	@Test
	public void testIsDeleted() {
		assertFalse(RawTherapeeParser.isDeleted(TestFileHelper.getTestResource("neutral.pp3")));
		assertTrue(RawTherapeeParser.isDeleted(TestFileHelper.getTestResource("neutral_deleted.pp3")));
	}
}
