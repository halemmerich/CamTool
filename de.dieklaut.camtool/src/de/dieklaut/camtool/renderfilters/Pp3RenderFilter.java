package de.dieklaut.camtool.renderfilters;

import java.nio.file.Path;
import java.util.Collection;

import de.dieklaut.camtool.RawTherapeeParser;
import de.dieklaut.camtool.operations.RenderFilter;

public class Pp3RenderFilter implements RenderFilter {

	private String key;
	private String value;

	public Pp3RenderFilter(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public boolean isFiltered(Path primaryFile, Collection<Path> collection) {
		Path pp3 = null;
		for (Path c : collection) {
			if (c.getFileName().toString().endsWith(".pp3")) {
				pp3 = c;
				break;
			}
		}
		
		if (pp3==null)
			return true;
		
		return RawTherapeeParser.get(pp3, key).equals(value);
	}

	@Override
	public String getShortString() {
		return key + "=" + value;
	}
}
