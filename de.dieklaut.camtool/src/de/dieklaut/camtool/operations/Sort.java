package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Sorting;

/**
 * Analyses the {@link Constants#FOLDER_ORIGINAL} contents and creates a {@link Sorting}.
 * @author mboonk
 *
 */
public class Sort extends AbstractOperation {
	
	private String name = "normal";

	@Override
	public void perform(Context context) {
		Path sortingFolder = context.getRoot().resolve(Constants.FOLDER_SORTED).resolve(name);
		try {
			Files.createDirectories(sortingFolder);
		} catch (IOException e) {
			Logger.log("Creation of folder for sorting failed" , e);
			return;
		}
		

		try (Stream<Path> files = Files.list(context.getTimeLine())) {
			files.forEach(file -> {
				try {
					Path destination = sortingFolder.resolve(file.getFileName());
					Files.createSymbolicLink(destination, sortingFolder.relativize(file.toRealPath()));
					file.toFile().setReadOnly();
				} catch (IOException e) {
					Logger.log("Linking file " + file + " to " + Constants.FOLDER_TIMELINE + " did cause an error", e);
				}
			});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	public void setName(String name) {
		this.name = name;
	}

}
