package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.operations.RenderFilter;
import de.dieklaut.camtool.renderjob.NullRenderJob;
import de.dieklaut.camtool.renderjob.RenderJob;

public class RenderSubstituteModifier implements RenderModifier {

	private Path rendersub;
	private Path rendersubExt;

	public RenderSubstituteModifier(Path rendersub, Path rendersubExt) {
		this.rendersub = rendersub;
		this.rendersubExt = rendersubExt;
	}

	@Override
	public RenderJob getRenderJob(Collection<RenderFilter> renderFilters) {
		try {
			Collection<Path> paths = getSubstitutePaths(rendersub, true);
			if (paths.isEmpty()) {
				Logger.log("No valid paths for substitution found", Level.INFO);
				return new NullRenderJob();
			}
			SingleGroup tempGroup = new SingleGroup(paths);
			return tempGroup.getRenderJob(renderFilters);
		} catch (IOException e) {
			Logger.log("Error during creation of copy render job for substitute file " + rendersub, e);
			return new NullRenderJob();
		}
	}

	private Collection<Path> getSubstitutePaths(Path sub, boolean allowRegex) throws IOException {
		HashSet<Path> paths = new HashSet<>();
		for (String currentLine : Files.readAllLines(sub)) {
			if (currentLine.trim().isEmpty()) {
				continue;
			}
			
			Path current = Paths.get(currentLine);
			
			if (Files.exists(current)) {
				paths.add(sub.toAbsolutePath().getParent().resolve(current));
			} else {
				Pattern p = Pattern.compile(currentLine);
				Collection<Path> matching = Files.list(sub.toAbsolutePath().getParent()).filter(f -> p.matcher(sub.toAbsolutePath().getParent().relativize(f).toString()).find()).collect(Collectors.toSet());
				paths.addAll(matching);
			}
			
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
			result.addAll(getSubstitutePaths(rendersub, true));
			if (rendersubExt != null && Files.exists(rendersubExt)) result.addAll(getSubstitutePaths(rendersubExt, false));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return result;
	}

}
