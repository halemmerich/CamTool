package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.DefaultSorter;
import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.util.FileUtils;

public class Init extends AbstractOperation {

	@Override
	public void perform(Context context) {
		try {
			moveToOriginalsFolder(context);
			createTimeLine(context);
		} catch (IOException e) {
			Logger.log("Failure during creation of the " + Constants.FOLDER_ORIGINAL + " folder", e);
		}
	}

	protected void createTimeLine(Context context) throws IOException {
		
		Path timelineFolder = Files
				.createDirectory(Paths.get(context.getRoot().toString(), Constants.FOLDER_TIMELINE));
		
		Map<String, Set<Path>> groupNames = DefaultSorter.detectGroupNames(context.getOriginals(), new HashSet<>());
		
		Collection<Group> groups = new HashSet<>();
		DefaultSorter.createSingleGroups(groups, groupNames, new HashMap<>(), new HashSet<>());
		
		for (Group group : groups) {
			String timestamp = FileUtils.getTimestamp(group.getTimestamp());
			for (Path current : group.getAllFiles()) {
				Path destination = timelineFolder.resolve(timestamp + "_" + current.getFileName());
				Files.createSymbolicLink(destination, timelineFolder.relativize(current));
			}
		}
	}

	protected void moveToOriginalsFolder(Context context) throws IOException {
		try (Stream<Path> files = Files.list(context.getRoot())) {
			Path originalFolder = Files
					.createDirectory(Paths.get(context.getRoot().toString(), Constants.FOLDER_ORIGINAL));
			files.forEach(file -> {
				try {
					if (file.getFileName().toString().equals(Constants.FOLDER_ORIGINAL) ||
							file.getFileName().toString().equals(Constants.AUTOMATION_FILE_NAME)) {
						return;
					}
					Path destination = Paths.get(originalFolder.toString(), file.getFileName().toString());
					Files.move(file, destination);
					destination.toFile().setReadOnly();
				} catch (IOException e) {
					Logger.log("Copying file " + file + " to " + Constants.FOLDER_ORIGINAL + " did cause an error", e);
				}
			});
		}
	}
}
