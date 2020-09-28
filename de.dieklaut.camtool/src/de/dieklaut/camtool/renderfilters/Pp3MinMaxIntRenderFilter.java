package de.dieklaut.camtool.renderfilters;

import java.nio.file.Path;
import java.util.Collection;

import de.dieklaut.camtool.FileTypeHelper;
import de.dieklaut.camtool.RawTherapeeParser;
import de.dieklaut.camtool.operations.RenderFilter;

public class Pp3MinMaxIntRenderFilter implements RenderFilter {
	
	private String key;
	private int minValue;
	private int maxValue;

	public Pp3MinMaxIntRenderFilter(String key, int minValue, int maxValue) {
		this.key = key;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	public boolean isFiltered(Path primaryFile, Collection<Path> collection) {
		Path pp3 = null;
		if (FileTypeHelper.isRawTherapeeProfile(primaryFile)) {
			pp3 = primaryFile;
		}
		for (Path c : collection) {
			if (FileTypeHelper.isRawTherapeeProfile(c)) {
				pp3 = c;
				break;
			}
		}
		if (pp3 == null) {
			return false;
		}
		return minValue <= Integer.parseInt(RawTherapeeParser.get(pp3, key)) && Integer.parseInt(RawTherapeeParser.get(pp3, key)) <= maxValue;
	}


	@Override
	public String getShortString() {
		return minValue + "<=" + key + "<=" + maxValue;
	}
}
