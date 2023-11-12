package de.dieklaut.camtool;
 
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.exec.CommandLine;
import org.junit.Test;

import de.dieklaut.camtool.external.ExternalTool;

public class ImagickResizerTest extends FileBasedTest {

	@Test
	public void test() throws IOException {
		ImagemagickResizer ir = new ImagemagickResizer();
		Path source = TestFileHelper.getTestResource("NEX5R.JPG");
		Path target = getTestFolder().resolve("target");
		ir.resize(300, source, target, 80);
		
		CommandLine commandlineIdentify = new CommandLine("identify");
		commandlineIdentify.addArgument("-ping");
		commandlineIdentify.addArgument("-format");
		commandlineIdentify.addArgument("%w %h", false);
		commandlineIdentify.addArgument(target.toAbsolutePath().toString(), false);
		
		ExternalTool identify = new ExternalTool() {
			
			@Override
			public CommandLine getCommandLine() {
				return commandlineIdentify;
			}
		};
		
		identify.process(true);	
		assertEquals("451 300", identify.getOutput());
	}
}
