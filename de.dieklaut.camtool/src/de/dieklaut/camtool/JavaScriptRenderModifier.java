package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

import de.dieklaut.camtool.operations.RenderFilter;
import de.dieklaut.camtool.renderjob.RenderJavaScriptScriptMultiRenderJob;
import de.dieklaut.camtool.renderjob.RenderJob;

public class JavaScriptRenderModifier implements RenderModifier {

	Path renderScript;
	MultiGroup group;
	
	public JavaScriptRenderModifier(MultiGroup group, Path renderScript) {
		this.renderScript = renderScript;
		this.group = group;
	}
	
	@Override
	public RenderJob getRenderJob(Collection<RenderFilter> renderFilters) {
		return new RenderJavaScriptScriptMultiRenderJob(renderScript, group);
	}

	@Override
	public void move(Path destination) {
		try {
			Files.move(renderScript, destination);
		} catch (IOException e) {
			Logger.log("Failure during move of " + renderScript + " to " + destination, e);
		}
	}

	@Override
	public Path getContainingFolder() {
		return renderScript.getParent();
	}

	@Override
	public Collection<Path> getAllFiles() {
		return Arrays.asList(new Path [] {renderScript});
	}

}
