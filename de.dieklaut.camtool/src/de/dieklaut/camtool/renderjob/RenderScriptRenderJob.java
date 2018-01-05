package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.exec.CommandLine;

import de.dieklaut.camtool.external.ExternalTool;

public class RenderScriptRenderJob implements RenderJob {

	private Path mainFile;
	private Path[] helperFiles;

	public RenderScriptRenderJob(Path mainFile, Path[] helperFiles) {
		this.mainFile = mainFile;
		this.helperFiles = helperFiles;
	}

	@Override
	public void store(Path destination) throws IOException {
		ExternalTool tool = new ExternalTool() {
			
			@Override
			public CommandLine getCommandLine() {
				CommandLine commandline = new CommandLine(mainFile.toAbsolutePath().toFile());
				commandline.addArgument("--");
				
				for (Path current : helperFiles) {
					commandline.addArgument(current.toAbsolutePath().toString(), false);
				}
				
				return commandline;
			}
		};
		if (!tool.process()) {
			throw new IllegalStateException("Storing of " + mainFile + " failed");
		}
	}

}
