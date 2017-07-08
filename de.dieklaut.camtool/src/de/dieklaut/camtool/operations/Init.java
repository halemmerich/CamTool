package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Logger;

public class Init extends AbstractOperation{

	@Override
	public void perform(Context context) {
		if (context.isInitialized()) {
			throw new IllegalArgumentException("This context is already initialized");
		}
		try (Stream<Path> files = Files.list(context.getRoot())) {
			Path originalFolder = Files.createDirectory(Paths.get(context.getRoot().toString(), Constants.FOLDER_ORIGINAL));
			files.forEach(file -> {
				try {
					if (file.getFileName().toString().equals(Constants.FOLDER_ORIGINAL)) {
						return;
					}
					Path destination = Paths.get(originalFolder.toString(), file.getFileName().toString());
					Files.move(file, destination);
					destination.toFile().setReadOnly();
				} catch (IOException e) {
					Logger.log("Copying file " + file + " to " + Constants.FOLDER_ORIGINAL + " did cause an error" , e);
				}
			});
		} catch (IOException e) {
			Logger.log("Failure during creation of the " + Constants.FOLDER_ORIGINAL + " folder", e);
		}
	}
}
