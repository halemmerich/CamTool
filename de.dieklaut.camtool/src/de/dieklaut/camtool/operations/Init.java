package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
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
		
		createTimeLineEntries(context.getOriginals(), timelineFolder);
	}

	private void createTimeLineEntries(Path originals, Path timelineFolder) {
		try {
			Files.list(originals).forEach(current -> {
				if (Files.isDirectory(current)) {
					try {
						Files.list(current).forEach(sub -> createTimeLineEntries(current, timelineFolder));
					} catch (IOException e) {
						Logger.log("Failed to iterate through original folder " + current + " for creating timeline symlinks", e);
					}
				} else {
					Path destination = timelineFolder.resolve(FileUtils.getTimestamp(current) + "_" + current.getFileName());
					try {
						Files.createSymbolicLink(destination, timelineFolder.relativize(current));
					} catch (IOException e) {
						Logger.log("Failed to create timeline symlink for " + current, e);
					}
				}
			});
		} catch (IOException e) {
			Logger.log("Failed to iterate through original folder for creating timeline symlinks", e);
		}
	}

	protected void moveToOriginalsFolder(Context context) throws IOException {
		Path originalFolder = Files
				.createDirectory(Paths.get(context.getRoot().toString(), Constants.FOLDER_ORIGINAL));
		try (Stream<Path> files = Files.list(context.getRoot())) {
			files.forEach(file -> {
				try {
					if (file.getFileName().toString().equals(Constants.FOLDER_ORIGINAL) ||
							file.getFileName().toString().equals(Constants.AUTOMATION_FILE_NAME)) {
						return;
					}
					
					Path destination = Paths.get(originalFolder.toString(), file.getFileName().toString());
					Files.move(file, destination);
				} catch (IOException e) {
					Logger.log("Copying file " + file + " to " + Constants.FOLDER_ORIGINAL + " did cause an error", e);
				}
			});
		}

		FileUtils.setReadOnlyRecursive(originalFolder);
	}
}
