package de.dieklaut.camtool.renderfilters;

import java.nio.file.Path;
import java.util.Collection;

import de.dieklaut.camtool.RawTherapeeParser;
import de.dieklaut.camtool.operations.RenderFilter;

public class Pp3MinIntRenderFilter implements RenderFilter {
	
	private String key;
	private int minValue;

	public Pp3MinIntRenderFilter(String key, int value) {
		this.key = key;
		this.minValue = value;
	}

	@Override
	public boolean isFiltered(Path primaryFile, Collection<Path> collection) {
		Path pp3 = null;
		for (Path c : collection) {
			if (c.getFileName().endsWith(".pp3")) {
				pp3 = c;
				break;
			}
		}
		
		return Integer.parseInt(RawTherapeeParser.get(pp3, key)) >= minValue;
	}


	@Override
	public String getShortString() {
		return key + ">=" + minValue;
	}
}
