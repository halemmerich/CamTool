package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.MultiGroup;
import de.dieklaut.camtool.Sorter;

public class Simplify extends AbstractOperation {

	private String sortingName = Constants.DEFAULT_SORTING_NAME;
	private Sorter sorter;
	
	public Simplify(Sorter sorter) {
		this.sorter = sorter;
	}

	public void setSortingName(String sortingName) {
		this.sortingName = sortingName;
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
		
		for (Group g : groups) {
			if (g instanceof MultiGroup) {
				MultiGroup multiGroup = (MultiGroup) g;
				if (multiGroup.getGroups().size() == 1 && !multiGroup.hasModifiers()) {
					multiGroup.getGroups().iterator().next().moveToFolder(multiGroup.getContainingFolder().getParent());
				}
			}
		}
	}

}
