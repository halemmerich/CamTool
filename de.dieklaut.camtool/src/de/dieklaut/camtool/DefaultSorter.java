package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.util.FileUtils;

/**
 * Default implementation sorting files into groups.
 * @author mboonk
 *
 */
public class DefaultSorter implements Sorter{

	public boolean useRawTherapee = false;

	@Override
	public Collection<Group> identifyGroups(Path path) throws IOException {

		Collection<Group> groups = new HashSet<>();
		Collection<Path> camtoolFiles = new HashSet<>();

		Map<String, Set<Path>> groupNamesToPaths = detectGroupNames(path, camtoolFiles);

		Map<String, Group> groupNamesToGroup = new HashMap<>();

		createSingleGroups(groups, groupNamesToPaths, groupNamesToGroup, camtoolFiles);
		
		createCollectionsFromFolders(path, groups, groupNamesToGroup);
		
		createCollectionsFromFiles(groups, camtoolFiles, groupNamesToGroup);

		return groups;
	}

	public static Map<String, Set<Path>> detectGroupNames(Path path, Collection<Path> camtoolFiles)
			throws IOException {
		Logger.log("Searching for group names in " + path ,Level.TRACE);
		Map<String, Set<Path>> groupNamesToPaths = new HashMap<>();
		Files.list(path).forEach(currentPath -> {
			if (!Files.isDirectory(currentPath)) {
				String currentFileName = currentPath.getFileName().toString();

				if (currentFileName.equals(Constants.SORTED_FILE_NAME)) {
					return;
				}

				if (currentFileName.matches(".*\\" + Constants.FILE_NAME_CAMTOOL_SUFFIX + "[^\\.]*$")) {
					Logger.log("Found camtool file: " + currentFileName ,Level.TRACE);
					camtoolFiles.add(currentPath);
				} else {

					String currentGroupName = FileUtils.getGroupName(currentFileName);

					if (!groupNamesToPaths.containsKey(currentGroupName)) {
						Logger.log("Found new group name " + currentGroupName ,Level.TRACE);
						groupNamesToPaths.put(currentGroupName, new HashSet<>());
					}
					groupNamesToPaths.get(currentGroupName).add(currentPath);
				}
			} else {
				try {
					groupNamesToPaths.putAll(detectGroupNames(currentPath, camtoolFiles));
				} catch (IOException e) {
					Logger.log("Failure during recursing into subfolders", e, Level.ERROR);
				}
			}
		});
		return groupNamesToPaths;
	}

	public static void createSingleGroups(Collection<Group> groups, Map<String, Set<Path>> groupNamesToPaths,
			Map<String, Group> groupNamesToGroup, Collection<Path> camtoolFiles) {
		for (String currentGroupName : groupNamesToPaths.keySet()) {
			Set<Path> currentGroupPaths = groupNamesToPaths.get(currentGroupName);

			for (Path current: camtoolFiles) {
				if (currentGroupName.equals(FileUtils.getGroupName(current))) {
					currentGroupPaths.add(current);
				}
			}
			
			SingleGroup newGroup = new SingleGroup(currentGroupPaths);
			groups.add(newGroup);
			groupNamesToGroup.put(currentGroupName, newGroup);
		}
	}

	/**
	 * Creates collections of existing groups using the
	 * 
	 * @param groups
	 * @param camtoolFiles
	 * @param groupNamesToGroup
	 * @throws IOException
	 */
	public static void createCollectionsFromFiles(Collection<Group> groups, Collection<Path> camtoolFiles,
			Map<String, Group> groupNamesToGroup) throws IOException {
		Map<Group, MultiGroup> groupToCollection = new HashMap<>();
		for (Path camtoolFile : camtoolFiles) {
			if (camtoolFile.getFileName().toString().endsWith(Constants.FILE_NAME_COLLECTION_SUFFIX)) {
				Set<Group> collectionGroups = new HashSet<>();
				for (String currentFileFromCollection : Files.readAllLines(camtoolFile)) {
					currentFileFromCollection = currentFileFromCollection.trim();
					if (currentFileFromCollection.isEmpty()) {
						continue;
					}
					Group groupForCollection = groupNamesToGroup.get(FileUtils.getGroupName(currentFileFromCollection));
					
					if (groupForCollection == null) {
						throw new IllegalStateException("No fitting group found for collection entry " + currentFileFromCollection);
					}
					
					collectionGroups.add(groupForCollection);
					groups.remove(groupForCollection);
				}
				MultiGroup newGroup = new MultiGroup(collectionGroups, camtoolFile);
				groups.add(newGroup);
				groupNamesToGroup.put(newGroup.getName(), newGroup);
				
				for (Group g : collectionGroups) {
					groupToCollection.put(g, newGroup);
				}
			}
		}
		for (Path camtoolFile : camtoolFiles) {
			if (camtoolFile.getFileName().toString().endsWith(Constants.FILE_NAME_RENDERSCRIPT_SUFFIX)) {
				Group group = groupNamesToGroup.get(FileUtils.getGroupName(camtoolFile));
				
				if (group != null && group instanceof MultiGroup) {
					((MultiGroup) group).setRenderscriptFile(camtoolFile);
				} else {
					Logger.log("Ignored camtool file " + camtoolFile + " because it does not belong to a multi group", Level.INFO);
				}
			}
		}
	}

	private static void createCollectionsFromFolders(Path path, Collection<Group> groups, Map<String, Group> groupNamesToGroup) throws IOException {
		Files.list(path).filter(file -> Files.isDirectory(file)).forEach(currentPath -> {
			try {
				Collection<Group> groupsForCollection = new HashSet<Group>();
				Files.list(currentPath).forEach(currentFile -> {					
					groupsForCollection.add(groupNamesToGroup.get(FileUtils.getGroupName(currentFile)));
				});
				
				groups.removeAll(groupsForCollection);
				groups.add(new MultiGroup(groupsForCollection));
			} catch (IOException e) {
				Logger.log("Failure during detection of collections from folder " + currentPath, e, Level.ERROR);
			}
		});
	}
}
