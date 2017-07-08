package de.dieklaut.camtool;

import org.junit.Test;

public class CamToolTest extends FileBasedTest{
	
	@Test
	public void testNoArgs() {
		CamTool.main(new String[] {});
	}
}
