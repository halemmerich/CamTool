package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.MultiGroup;
import de.dieklaut.camtool.Sorter;

public class Move extends AbstractOperation{

	private String sortingName = Constants.DEFAULT_SORTING_NAME;
	private Sorter sorter;
	private String nameOfGroup;
	private Path targetPath = Paths.get("../");
	
	public Move(Sorter sorter) {
		super();
		this.sorter = sorter;
	}
	
	public void setSortingName(String sortingName) {
		this.sortingName = sortingName;
	}

	public void setNameOfGroup(String nameOfGroup) {
		this.nameOfGroup = nameOfGroup;
	}

	public void setTargetPath(Path targetPath) {
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
		
		Group group = findGroupToMove(groups);
		if (group == null) {
			throw new IllegalStateException("Could not find group for name " + nameOfGroup);
		}
		group.moveToFolder(targetPath);
	}

	private Group findGroupToMove(Collection<Group> groups) {
		for (Group group : groups) {
			if (nameOfGroup.equals(group.getName())) {
				return group;
			}
			if (group instanceof MultiGroup) {
				Group result = findGroupToMove(((MultiGroup) group).getGroups());
				if (result != null) {
					return result;
				}
			}
		}
		return null;
	}
}
