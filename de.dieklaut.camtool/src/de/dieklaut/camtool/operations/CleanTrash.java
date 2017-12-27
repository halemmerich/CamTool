package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Sorter;

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
		
		for (Group group : groups) {
			if (group.isMarkedAsDeleted()) {
				for (Path file : group.getAllFiles()) {
					try {
						Files.delete(file);
						Path parent = file.getParent();
						if (Files.list(parent).count() == 0) {
							Files.delete(parent);
						}
					} catch (IOException e) {
						Logger.log("Could not delete file " + file, e);
					}
				}
			}
		}
	}

}
