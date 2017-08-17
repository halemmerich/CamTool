package de.dieklaut.camtool;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class RawTherapeeWrapperTest{

	@Test
	public void testStore() throws IOException {
		Path rawFile = Paths.get("test.arw");
		Path destination = Paths.get("whatever");
		Path sidecar = Paths.get("test.pp3");
		
		RawTherapeeWrapper wrapper = new RawTherapeeWrapper();
		
		wrapper.setInputFile(rawFile.toString());
		wrapper.setOutputFile(destination.toString());
		wrapper.addProfileOption(sidecar.toString());
		wrapper.setJpgQuality(95, 3);
		
		assertEquals("rawtherapee-cli -o \"whatever\" -j95 -js3 -d -p \"test.pp3\" -c \"test.arw\"", wrapper.getCommandLine());
	}
}
