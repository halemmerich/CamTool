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
		Collection<Group> result = new HashSet<Group>();
		
		HashMap<String, Set<Path>> nameToPaths = new HashMap<>();
		HashMap<String, Group> nameToGroup = new HashMap<>();
		
		Set<Path> collectionFiles = new HashSet<>();

		Files.list(path).forEach(current -> {
			if (Files.isDirectory(current)) {
				MultiGroup multiGroup;
				try {
					Collection<Group> groups = identifyGroups(current);
					Path renderscript = current.resolve(Constants.FILE_NAME_RENDERSCRIPT);
					Path rendersub = current.resolve(Constants.FILE_NAME_RENDERSUBSTITUTE);
					if (groups.size() > 1 || groups.size() == 0) {
						multiGroup = new MultiGroup(groups);
						if (Files.exists(renderscript)) {
							multiGroup.setRenderModifier(new JavaScriptRenderModifier(multiGroup, renderscript));
						} else if (Files.exists(rendersub)) {
							multiGroup.setRenderModifier(new RenderSubstituteModifier(rendersub));
						}
						result.add(multiGroup);
						nameToGroup.put(multiGroup.getName(), multiGroup);
					} else if (groups.size() == 1){
						result.addAll(groups);
						Group group = groups.iterator().next();
						nameToGroup.put(group.getName(), group);
					}
				} catch (IOException e) {
					Logger.log("Error during group analysis of " + path, e);
				}
			} else {
				String groupName = FileUtils.getGroupName(current);
				if (current.getFileName().toString().equals(Constants.FILE_NAME_RENDERSCRIPT) || current.getFileName().toString().equals(Constants.SORTED_FILE_NAME)) {
					return;
				}
				if (!current.getFileName().toString().contains(Constants.FILE_NAME_COLLECTION_SUFFIX)) {
					if (!nameToPaths.containsKey(groupName)) {
						nameToPaths.put(groupName, new HashSet<>());
					}
					nameToPaths.get(groupName).add(current);
				} else {
					collectionFiles.add(current);
				}
			}
		});
		
		//Creation of single groups
		
		for (String name : nameToPaths.keySet()) {
			SingleGroup group = new SingleGroup(nameToPaths.get(name));
			result.add(group);
			nameToGroup.put(name, group);
		}
		
		//Creation of collections from files
		
		createCollectionsFromFiles(result, collectionFiles, nameToGroup);
		
		return result;
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

				String currentGroupName = null;
				if (currentFileName.contains(Constants.FILE_NAME_CAMTOOL)) {
					Logger.log("Found camtool file: " + currentFileName ,Level.TRACE);
					camtoolFiles.add(currentPath);
					currentGroupName = FileUtils.getGroupName(currentPath);
					
				} else {
					currentGroupName = FileUtils.getGroupName(currentPath);
				}
				
				if (!groupNamesToPaths.containsKey(currentGroupName)) {
					Logger.log("Found new group name " + currentGroupName, Level.TRACE);
					groupNamesToPaths.put(currentGroupName, new HashSet<>());
				}
				if (!currentFileName.contains(Constants.FILE_NAME_CAMTOOL)) {
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
				}
				MultiGroup newGroup = new MultiGroup(collectionGroups, camtoolFile);
				groups.add(newGroup);
				groups.removeAll(collectionGroups);
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
					((MultiGroup) group).setRenderModifier(new JavaScriptRenderModifier((MultiGroup) group, camtoolFile));
				} else {
					Logger.log("Ignored camtool file " + camtoolFile + " because it does not belong to a multi group", Level.INFO);
				}
			}
		}
	}
}
