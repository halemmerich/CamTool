package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;

import de.dieklaut.camtool.operations.RenderFilter;
import de.dieklaut.camtool.renderjob.NullRenderJob;
import de.dieklaut.camtool.renderjob.RenderJob;

public class RenderSubstituteModifier implements RenderModifier {

	private Path rendersub;

	public RenderSubstituteModifier(Path rendersub) {
		this.rendersub = rendersub;
	}

	@Override
	public RenderJob getRenderJob(Collection<RenderFilter> renderFilters) {
		try {
			Collection<Path> paths = getSubstitutePaths();
			SingleGroup tempGroup = new SingleGroup(paths);
			return tempGroup.getRenderJob(renderFilters);
		} catch (IOException e) {
			Logger.log("Error during creation of copy render job for substitute file " + rendersub, e);
			return new NullRenderJob();
		}
	}

	private Collection<Path> getSubstitutePaths() throws IOException {
		HashSet<Path> paths = new HashSet<>();
		for (String currentLine : Files.readAllLines(rendersub)) {
			if (currentLine.trim().isEmpty()) {
				continue;
			}
			Path current = Paths.get(currentLine);
			paths.add(rendersub.toAbsolutePath().getParent().resolve(current));
		}
	
		return paths;
	}

	@Override
	public void move(Path destination) {
		// TODO Auto-generated method stub

	}

	@Override
	public Path getContainingFolder() {
		return rendersub.getParent();
	}

	@Override
	public Collection<Path> getAllFiles() {
		Collection<Path> result = new HashSet<>();
		result.add(rendersub);
		try {
			result.addAll(getSubstitutePaths());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return result;
	}

}
