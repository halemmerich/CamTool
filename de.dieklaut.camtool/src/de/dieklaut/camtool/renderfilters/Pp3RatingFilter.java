package de.dieklaut.camtool.renderfilters;

import java.nio.file.Path;
import java.util.Collection;
import java.util.StringJoiner;

import de.dieklaut.camtool.RawTherapeeParser;
import de.dieklaut.camtool.operations.RenderFilter;

public class Pp3RatingFilter implements RenderFilter {
	
	private String key;
	private int [] allowedValues;

	public Pp3RatingFilter(String key, int ... allowedValues) {
		this.key = key;
		this.allowedValues = allowedValues;
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
		
		if (pp3 == null) {
			return true;
		}
		
		int value = Integer.parseInt(RawTherapeeParser.get(pp3, key));
		for (int i : allowedValues) {
			if (i == value) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getShortString() {
		StringJoiner j = new StringJoiner("");
		for (int i : allowedValues) {
			j.add("" + i);
		}
		return "R" + j.toString();
	}

}
