package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.MultiGroup;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.SortingHelper;

public class Explode extends AbstractOperation {

	private String sortingName = Constants.DEFAULT_SORTING_NAME;
	private Sorter sorter;
	private String groupName;
	private Path groupPath;
	
	public Explode(Sorter sorter) {
		this.sorter = sorter;
	}

	public void setSortingName(String sortingName) {
		this.sortingName = sortingName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName; 
	}
	
	public void setGroupPath(Path groupPath) {
		this.groupPath = groupPath; 
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
		
		Group g = null;
		if (groupName != null) {
			g = SortingHelper.findGroupByName(groups, groupName);
		}
		if (groupPath != null) {
			g = SortingHelper.findGroupByPath(groups, groupPath, sortingFolder);
		}
		
		if (g != null) {
			if (g instanceof MultiGroup) {
				MultiGroup multi = (MultiGroup) g;
				if (multi.hasOwnFolder() && !multi.hasModifiers()) {
					for (Group current : multi.getGroups()) {
						current.moveToFolder(multi.getContainingFolder().getParent());
					}
				} else {
					Logger.log("Group with name " + groupName + " has additional files", Level.ERROR);
				}
			} else {
				Logger.log("Group with name " + groupName + " is not a MultiGroup", Level.ERROR);
			}
		} else {
			if (groupName != null) {
				Logger.log("Did not find a group with name " + groupName, Level.ERROR);
			} else if (groupPath != null) {
				Logger.log("Did not find a group with path " + groupPath, Level.ERROR);
			} else {
				Logger.log("No group specified", Level.ERROR);
			}
		}
	}

}
