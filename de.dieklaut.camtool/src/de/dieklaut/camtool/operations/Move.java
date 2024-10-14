package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.SortingHelper;

public class Move extends AbstractOperation{

	private String sortingName = Constants.DEFAULT_SORTING_NAME;
	private Sorter sorter;
	private String regex;
	private List<String> identifiers;
	private Path targetPath = Paths.get("../");
	
	public Move(Sorter sorter) {
		super();
		this.sorter = sorter;
	}
	
	public void setSortingName(String sortingName) {
		this.sortingName = sortingName;
	}

	public void setIdentifiers(List<String> identifiers) {
		this.identifiers = identifiers;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public void setTargetPath(Path targetPath) {
		if (!targetPath.isAbsolute())
			throw new IllegalArgumentException("Target path must be absolute");
		this.targetPath = targetPath;
	}

	@Override
	public void perform(Context context) {
		Collection<Group> groups;
		Path sortingFolder;
		try {
			sortingFolder = context.getRoot().resolve(Constants.FOLDER_SORTED).resolve(sortingName);
			groups = sorter.identifyGroups(sortingFolder);
		} catch (IOException e) {
			throw new IllegalStateException("Could not read groups", e);
		}

		Stream<Group> filteredGroups;
		if (regex != null) {
			filteredGroups = groups.stream().filter(g -> g.getName().matches(regex));
		} else {
			filteredGroups = identifiers.stream().map(i -> {
					var g = SortingHelper.findGroupByPath(groups, Paths.get(i), sortingFolder);
					if (g == null)
						g = SortingHelper.findGroupByName(groups, i);
					return g;
				}).filter(g -> g != null);
		}
		filteredGroups.forEach(g -> g.moveToFolder(targetPath));
	}

	public void setDifference(String optionValue) {
		// TODO Auto-generated method stub
		
	}

}
