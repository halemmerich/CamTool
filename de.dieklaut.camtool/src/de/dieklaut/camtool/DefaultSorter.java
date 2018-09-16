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

		Files.list(path).forEach(current -> {
			if (Files.isDirectory(current) && !Files.exists(current.resolve(Constants.SORTED_FILE_NAME))) {
				MultiGroup multiGroup;
				try {
					Collection<Group> groups = identifyGroups(current);
					Path renderscript = current.resolve(Constants.FILE_NAME_RENDERSCRIPT);
					Path rendersub = current.resolve(Constants.FILE_NAME_RENDERSUBSTITUTE);
					multiGroup = new MultiGroup(groups);
					if (Files.exists(renderscript)) {
						multiGroup.setRenderModifier(new JavaScriptRenderModifier(multiGroup, renderscript));
					} else if (Files.exists(rendersub)) {
						multiGroup.setRenderModifier(new RenderSubstituteModifier(rendersub));
					}
					result.add(multiGroup);
					nameToGroup.put(multiGroup.getName(), multiGroup);
				} catch (IOException e) {
					Logger.log("Error during group analysis of " + path, e);
				}
			} else if (!current.getFileName().toString().equals(Constants.SORTED_FILE_NAME)){
				String groupName = FileUtils.getNamePortion(current);
				if (current.getFileName().toString().equals(Constants.FILE_NAME_RENDERSCRIPT) || current.getFileName().toString().equals(Constants.FILE_NAME_RENDERSUBSTITUTE) || current.getFileName().toString().equals(Constants.SORTED_FILE_NAME)) {
					return;
				}
				if (!nameToPaths.containsKey(groupName)) {
					nameToPaths.put(groupName, new HashSet<>());
				}
				nameToPaths.get(groupName).add(current);
			}
		});
		
		//Creation of single groups
		
		for (String name : nameToPaths.keySet()) {
			SingleGroup group = new SingleGroup(nameToPaths.get(name));
			result.add(group);
			nameToGroup.put(name, group);
		}
		
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

}
