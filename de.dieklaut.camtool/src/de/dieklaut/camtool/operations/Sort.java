package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Stream;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.MultiGroup;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.SortingHelper;
import de.dieklaut.camtool.util.FileUtils;

/**
 * Analyses the {@link Constants#FOLDER_ORIGINAL} contents and creates a
 * {@link Sorting}.
 * 
 * @author mboonk
 *
 */
public class Sort extends AbstractOperation {

	private String name = Constants.DEFAULT_SORTING_NAME;
	private boolean moveCollectionsToFolder = false;
	private boolean moveAllGroupsToFolder = false;
	private boolean detectSeries = false;
	private Sorter sorter;
	
	public Sort(Sorter sorter) {
		this.sorter = sorter;
	}

	@Override
	public void perform(Context context) {
		Path sortingFolder = context.getRoot().resolve(Constants.FOLDER_SORTED).resolve(name);
		try {
			Files.createDirectories(sortingFolder);
		} catch (IOException e) {
			Logger.log("Creation of folder for sorting failed", e);
			return;
		}

		try (Stream<Path> files = Files.list(context.getTimeLine())) {
			files.forEach(file -> {
				try {
					Path destination = sortingFolder.resolve(file.getFileName());
					Files.createSymbolicLink(destination, sortingFolder.relativize(file.toRealPath()));
				} catch (IOException e) {
					Logger.log("Linking file " + file + " to " + Constants.FOLDER_TIMELINE + " did cause an error", e);
				}
			});
			Collection<Group> sorting = sorter.identifyGroups(sortingFolder);
			
			if (detectSeries) {
				SortingHelper.combineSeries(sorting);
			}
			
			if (moveCollectionsToFolder || moveAllGroupsToFolder) {
				moveCollections(sorting, sortingFolder);
			}
			
			Files.createFile(sortingFolder.resolve(Constants.SORTED_FILE_NAME));
		} catch (IOException e) {
			throw new IllegalStateException("A file operation failed", e);
		}
	}

	private void moveCollections(Collection<Group> groups, Path sortingFolder) {
		for (Group group : groups) {
			if ((group instanceof MultiGroup || moveAllGroupsToFolder) && !group.hasOwnFolder()) {
				Path destination = group.getContainingFolder().resolve(buildGroupName(group));
				if(!Files.exists(destination)) {
					try {
						Files.createDirectory(destination);
					} catch (IOException e) {
						throw new IllegalStateException("Moving group has failed", e);
					}
				}
				group.moveToFolder(sortingFolder.resolve(destination));
			}
		}
	}

	private String buildGroupName(Group group) {
		long currentMin = Long.MAX_VALUE;
		String currentName = "";
		for (Path current: group.getAllFiles()) {
			long stamp = FileUtils.getTimestampPortion(current);
			if (stamp < currentMin) {
				currentMin = stamp;
				currentName = FileUtils.getNamePortion(current);
			}
		}
		return currentMin + "_" + group.getType() + "_" + currentName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMoveCollectionsToFolder(boolean moveCollectionsToFolder) {
		this.moveCollectionsToFolder = moveCollectionsToFolder;
	}

	public void setDetectSeries(boolean detectSeries) {
		this.detectSeries = detectSeries;
	}
	
	public void setMoveAllGroupsToFolder(boolean moveAllGroupsToFolder) {
		this.moveAllGroupsToFolder = moveAllGroupsToFolder;
	}

}
