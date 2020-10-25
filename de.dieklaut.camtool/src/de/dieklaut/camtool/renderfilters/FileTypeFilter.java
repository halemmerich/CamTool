package de.dieklaut.camtool.renderfilters;

import java.nio.file.Path;
import java.util.Collection;

import de.dieklaut.camtool.operations.RenderFilter;

public class FileTypeFilter implements RenderFilter {

	private String suffix;

	public FileTypeFilter(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public boolean isFiltered(Path primaryFile, Collection<Path> collection) {
		return !primaryFile.getFileName().toString().endsWith(suffix);
	}

	@Override
	public String getShortString() {
		return "T" + suffix;
	}
}
