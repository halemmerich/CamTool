package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;

/**
 * This exports the rendering results of a sorting into an arbitrary folder.
 * @author mboonk
 *
 */
public class Export extends AbstractOperation {

	private String name = Constants.DEFAULT_SORTING_NAME;
	private String type = Constants.RENDER_TYPE_FULL;
	private Path destination = null;

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDestination(Path destination) {
		this.destination = destination;
	}

	@Override
	public void perform(Context context) {
		Path resultFolder = context.getRoot().resolve(Constants.FOLDER_RESULTS).resolve(name).resolve(type);
		
		if (!Files.exists(resultFolder) || (Files.exists(resultFolder) && !Files.isDirectory(resultFolder))) {
			Logger.log("Result folder " + resultFolder + " does not exist or is not a directory.", Level.ERROR);
			return;
		}
		
		if (destination == null) {
			destination = context.getRoot().resolve(Constants.DEFAULT_EXPORT_NAME);
		}
	
		destination = destination.resolve(context.getName());
		
		
		if (!Files.exists(destination)) {
			try {
				Files.createDirectories(destination);
			} catch (IOException e) {
				Logger.log("Could not create export destination directory " + destination, e);
				return;
			}
		}
		
		try {
			Files.list(resultFolder).forEach(file -> {
				try {
					Path realfile = file.toRealPath();
					Files.copy(realfile, destination.resolve(file.getFileName()));
				} catch (IOException e) {
					Logger.log("Linking file " + file + " to " + Constants.FOLDER_TIMELINE + " did cause an error", e);
				}
			});
		} catch (IOException e) {
			Logger.log("Could not read result directory " + resultFolder, e);
			return;
		}
	}

}
