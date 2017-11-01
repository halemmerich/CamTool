package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import org.apache.commons.exec.CommandLine;

import de.dieklaut.camtool.external.ExternalTool;
import de.dieklaut.camtool.renderjob.RenderJob;

public class RenderScriptMultiRenderJob implements RenderJob {

	private Collection<Group> groups;
	private Path renderscriptFile;

	public RenderScriptMultiRenderJob(Path renderscriptFile, Collection<Group> groups) {
		this.groups = groups;
		this.renderscriptFile = renderscriptFile;
	}

	@Override
	public void store(Path destination) throws IOException {
		Path workDir = Files.createTempDirectory("camtool");
		for (Group group : groups) {
			group.getRenderJob().store(workDir);
		}
		

		ExternalTool tool = new ExternalTool() {
			
			@Override
			public CommandLine getCommandLine() {
				CommandLine commandline = new CommandLine(renderscriptFile.toAbsolutePath().toFile());
				commandline.addArgument("--");
				
				try {
					Files.list(workDir).forEach(file -> {commandline.addArgument(file.toAbsolutePath().toString(), false);});
				} catch (IOException e) {
					throw new IllegalStateException("Multi group rendering failed", e);
				}
				
				return commandline;
			}
		};

		if (!tool.process()) {
			throw new IllegalStateException("Storing of multigroup failed");
		}
	}

}
