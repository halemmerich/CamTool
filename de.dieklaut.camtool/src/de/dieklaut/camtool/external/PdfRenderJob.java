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

public class PdfRenderJob extends RenderJob {

	private Path source;

	public PdfRenderJob(Path source) {
		this.source = source;
	}

	@Override
	public Set<Path> storeImpl(Path destination, Collection<RenderFilter> renderFilters) throws IOException {
		CommandLine commandline = new CommandLine("pdftoppm");
		commandline.addArgument("-png", false);
		commandline.addArgument("-singlefile", false);
		commandline.addArgument("-scale-to", false);
		commandline.addArgument("6000", false);
		commandline.addArgument(source.toAbsolutePath().toString(), false);
		
		if (Files.isDirectory(destination)) {
			destination = destination.resolve(FileUtils.removeSuffix(source.getFileName()));
		} else {
			destination = destination.getParent().resolve(FileUtils.removeSuffix(destination.getFileName()));
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
			produced.add(destination.getParent().resolve(FileUtils.removeSuffix(destination.getFileName()) + ".png"));
		}
		return produced;
	}

	@Override
	public Set<Path> getPredictedResultsImpl(Path destination, Collection<RenderFilter> renderFilters)
			throws IOException {
		if (Files.isDirectory(destination)) {
			destination = destination.resolve(FileUtils.removeSuffix(source.getFileName()) + ".png");
		} else {
			destination = destination.getParent().resolve(FileUtils.removeSuffix(destination.getFileName()) + ".png");
		}
		Set<Path> produced = new HashSet<>();
		produced.add(destination);
		return produced;
	}

}
