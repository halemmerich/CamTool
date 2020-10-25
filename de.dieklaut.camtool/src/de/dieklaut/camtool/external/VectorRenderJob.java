package de.dieklaut.camtool.external;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.exec.CommandLine;

import de.dieklaut.camtool.operations.RenderFilter;
import de.dieklaut.camtool.renderjob.RenderJob;
import de.dieklaut.camtool.util.FileUtils;

public class VectorRenderJob extends RenderJob {

	private Path source;

	public VectorRenderJob(Path source) {
		this.source = source;
	}

	@Override
	public Set<Path> storeImpl(Path destination, Collection<RenderFilter> renderFilters) throws IOException {
		CommandLine commandline = new CommandLine("convert");
		commandline.addArgument("-density");
		commandline.addArgument("600");
		commandline.addArgument(source.toAbsolutePath().toString(), false);
		commandline.addArgument("-auto-orient");
		commandline.addArgument("-strip");
		
		if (Files.isDirectory(destination)) {
			destination = destination.resolve(FileUtils.removeSuffix(source.getFileName()) + ".png");
		}
		commandline.addArgument(destination.toAbsolutePath().toString(), false);
		
		ExternalTool convert = new ExternalTool() {
			
			@Override
			public CommandLine getCommandLine() {
				return commandline;
			}
		};
		
		boolean result = convert.process();
		
		Set<Path> produced = new HashSet<>();
		if (result) {
			produced.add(destination);
		}
		return produced;
	}

	@Override
	public Set<Path> getPredictedResultsImpl(Path destination, Collection<RenderFilter> renderFilters)
			throws IOException {
		if (Files.isDirectory(destination)) {
			destination = destination.resolve(FileUtils.removeSuffix(source.getFileName()) + ".png");
		}
		Set<Path> produced = new HashSet<>();
		produced.add(destination);
		return produced;
	}

}
