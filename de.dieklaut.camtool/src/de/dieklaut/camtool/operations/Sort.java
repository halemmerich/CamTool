package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
 * Analyses the {@link Constants#FOLDER_ORIGINAL} contents and creates a sorting
 * in the folder with the given name or a default.
 * 
 * The sorting can handle files created in fast succession and combine them to a
 * {@link MultiGroup}s.
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
	private int detectSeriesTimeDiff = 2;

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
					Files.createSymbolicLink(destination, sortingFolder.relativize(file));
				} catch (IOException e) {
					Logger.log("Linking file " + file + " to " + Constants.FOLDER_TIMELINE + " did cause an error", e);
				}
			});
			Collection<Group> sorting = sorter.identifyGroups(sortingFolder);

			if (detectSeries) {
				SortingHelper.combineSeries(sorting, detectSeriesTimeDiff);
			}

			if (moveCollectionsToFolder || moveAllGroupsToFolder) {
				moveCollections(sorting, sortingFolder);
			} else {
				createCollectionFiles(sorting);
			}

			Files.createFile(sortingFolder.resolve(Constants.SORTED_FILE_NAME));
		} catch (IOException e) {
			throw new IllegalStateException("A file operation failed", e);
		}
	}

	private void createCollectionFiles(Collection<Group> sorting) {
		for (Group g : sorting) {
			if (g instanceof MultiGroup) {
				try {
					Path collectionFile = g.getContainingFolder()
							.resolve(g.getName() + Constants.FILE_NAME_COLLECTION_SUFFIX);

					if (Files.exists(collectionFile)) {
						continue;
					}
					Files.createFile(collectionFile);

					for (Path p : g.getAllFiles()) {
						Files.write(collectionFile, (p.getFileName().toString() + "\n").getBytes(),
								StandardOpenOption.APPEND);
					}
				} catch (IOException e) {
					Logger.log("Error during creation of a collection file for " + g.getName(), e);
				}
			}
		}
	}

	private void moveCollections(Collection<Group> groups, Path sortingFolder) {
		sortingFolder = sortingFolder.toAbsolutePath().normalize();
		for (Group group : groups) {
			Path destination = group.getContainingFolder().resolve(buildGroupName(group)).toAbsolutePath().normalize();
			if (((group instanceof MultiGroup && sortingFolder.equals(group.getContainingFolder().toAbsolutePath().normalize())) || moveAllGroupsToFolder)) {
				if (!Files.exists(destination)) {
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
		for (Path current : group.getAllFiles()) {
			long stamp = FileUtils.getTimestampPortion(current);
			if (stamp < currentMin) {
				currentMin = stamp;
			}
		}
		return currentMin + "_" + group.getType();
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

	public void setDetectSeriesTime(int timeDiff) {
		this.detectSeriesTimeDiff = timeDiff;
	}

}
