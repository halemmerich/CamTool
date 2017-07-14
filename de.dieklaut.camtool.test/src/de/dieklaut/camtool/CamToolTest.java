package de.dieklaut.camtool;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class CamToolTest extends FileBasedTest{
	
	@Before
	public void setUp() throws InterruptedException, IOException {
		if (System.getenv("NEEDS_WORKING_DIR") == null) {
			System.err.println("DO NOT EXECUTE THIS TEST WITHOUT SETTING A DEDICATED WORKING DIRECTORY!\nIT WILL DESTROY YOUR STUFF!");
			System.exit(1);
		}
	}

	@Test
	public void testNoArgs() {
		CamTool.main(new String[] {});
	}
	
	@Test
	public void testInitNoArgs() {
		CamTool.main(new String[] {"init"});
	}
	
	@Test
	public void testSortNoArgs() {
		CamTool.main(new String[] {"sort"});
	}
}
