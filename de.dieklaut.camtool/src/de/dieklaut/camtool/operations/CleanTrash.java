package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.MultiGroup;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.util.FileUtils;

/**
 * Deletes all {@link Group}s marked as deleted
 * @author mboonk
 *
 */
public class CleanTrash extends AbstractOperation {

	private String sortingName = Constants.DEFAULT_SORTING_NAME;
	private Sorter sorter;

	public CleanTrash(Sorter sorter) {
		this.sorter = sorter;
	}
	
	public void setName(String sortingName) {
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
		
		recursiveDeleteGroups(groups);
		try {
			FileUtils.removeEmptyFolders(sortingFolder);
		} catch (IOException e) {
			Logger.log("Failure during cleanup of empty folders in " + sortingFolder, e);
		}
	}

	private void recursiveDeleteGroups(Collection<Group> groups) {
		for (Group group : groups) {
			if (group instanceof MultiGroup) {
				if (Files.exists(group.getContainingFolder().resolve(Constants.FILE_NAME_RENDERSUBSTITUTE)))
					Logger.log("Multi group " + group.getName() + " has a substitution file, please check if still valid after deletion", Level.INFO);
				recursiveDeleteGroups(((MultiGroup) group).getGroups());
			} else if (group.isMarkedAsDeleted()) {
				delete(group);
			}
		}
	}

	private void delete(Group group) {
		for (Path file : group.getAllFiles()) {
			try {
				Files.delete(file);
				Path parent = file.getParent();
				if (FileUtils.getFileCount(parent) == 0) {
					Files.delete(parent);
				}
			} catch (IOException e) {
				Logger.log("Could not delete file " + file, e);
			}
		}
	}

}
