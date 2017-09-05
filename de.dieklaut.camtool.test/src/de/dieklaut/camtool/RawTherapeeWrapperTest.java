package de.dieklaut.camtool;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.exec.CommandLine;
import org.junit.Test;

public class RawTherapeeWrapperTest {

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

		CommandLine expected = new CommandLine("rawtherapee-cli");
		expected.addArgument("-o \"whatever\"");
		expected.addArgument("-j95");
		expected.addArgument("-js3");
		expected.addArgument("-d");
		expected.addArgument("-p \"test.pp3\"");
		expected.addArgument("-c \"test.arw\"");

		assertEquals(expected.toString(), wrapper.getCommandLine().toString());
	}
}
