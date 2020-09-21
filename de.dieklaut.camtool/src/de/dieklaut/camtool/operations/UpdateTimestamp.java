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
import de.dieklaut.camtool.SingleGroup;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.util.FileUtils;

/**
 * Deletes all {@link Group}s marked as deleted
 * 
 * @author mboonk
 *
 */
public class UpdateTimestamp extends AbstractOperation {

	private String sortingName = Constants.DEFAULT_SORTING_NAME;
	private Sorter sorter;

	public UpdateTimestamp(Sorter sorter) {
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

		recursiveUpdateTimestamp(groups, sortingFolder);
	}

	private void recursiveUpdateTimestamp(Collection<Group> groups, Path sortingFolder) {
		for (Group group : groups) {
			if (group instanceof MultiGroup) {
				Logger.log("Multi group handling not implemented", Level.INFO);
			} else if (group instanceof SingleGroup) {
				updateTimestamp((SingleGroup) group, sortingFolder);
			} else {
				Logger.log("Unexpected group type + " + group.getClass(), Level.WARNING);
			}
		}
	}

	private void updateTimestamp(SingleGroup group, Path sortingFolder) {
		String targetFileStamp = "";
		for (Path file : group.getAllFiles()) {
			if (Files.isSymbolicLink(file)) {
				Path target;
				try {
					target = Files.readSymbolicLink(file);
					if (targetFileStamp.isEmpty()) {
						targetFileStamp = FileUtils.getTimestampPortion(target);
					} else {
						if (!targetFileStamp.equals(FileUtils.getTimestampPortion(target))) {
							Logger.log("Group " + group.getName() + " contains ambigous link target timestamps",
									Level.WARNING);
							return;
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		if (targetFileStamp.isEmpty()) {
			Logger.log("Could not get a target file stamp for group " + group.getName(), Level.INFO);
		}

		for (Path file : group.getAllFiles()) {
			try {
				FileUtils.changeTimestamp(file, sortingFolder, targetFileStamp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
