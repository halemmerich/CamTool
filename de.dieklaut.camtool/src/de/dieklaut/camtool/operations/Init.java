package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.FileOperationException;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.util.FileUtils;

public class Init extends AbstractOperation {
	
	public Init() {
		// TODO Auto-generated constructor stub
	}

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
		try (Stream<Path> files = Files.list(context.getOriginals())) {
			Path timelineFolder = Files
					.createDirectory(Paths.get(context.getRoot().toString(), Constants.FOLDER_TIMELINE));
			files.forEach(file -> {
				try {
					//TODO handle sub folders
					String timestamp = FileUtils.getTimestamp(FileUtils.getCreationDate(file));
					Path destination = timelineFolder.resolve(timestamp + "_" + file.getFileName());
					Files.createSymbolicLink(destination, timelineFolder.relativize(file));
				} catch (IOException | FileOperationException e) {
					Logger.log("Linking file " + file + " to " + Constants.FOLDER_TIMELINE + " did cause an error", e);
				}
			});
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
