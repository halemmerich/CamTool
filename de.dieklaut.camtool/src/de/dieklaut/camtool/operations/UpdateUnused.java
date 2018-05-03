package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.util.FileUtils;

/**
 * Analyses all {@link Sorting}s and creates a {@link Constants#FOLDER_UNUSED}.
 * 
 * @author mboonk
 *
 */
public class UpdateUnused extends AbstractOperation {

	private Set<Path> usedOriginalPaths = new HashSet<>();

	@Override
	public void perform(Context context) {
		Path sortingsFolder = context.getRoot().resolve(Constants.FOLDER_SORTED);
		try {
			Files.list(sortingsFolder).forEach(file -> {
				identifyUsedTargets(file);
			});

			Set<Path> unused = new HashSet<>();
			Files.list(context.getRoot().resolve(Constants.FOLDER_TIMELINE)).forEach(file -> {
				Path timelineFile;
				try {
					timelineFile = file.toRealPath();
					if (!usedOriginalPaths.contains(timelineFile)) {
						unused.add(file);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

			Path unusedFolder = context.getRoot().resolve(Constants.FOLDER_UNUSED);
			if (Files.exists(unusedFolder)) {
				FileUtils.deleteRecursive(unusedFolder, false);
			}
			Files.createDirectories(unusedFolder);
			for (Path current : unused) {
				Files.createSymbolicLink(unusedFolder.resolve(current.getFileName()), unusedFolder.relativize(current));
			}
		} catch (IOException e) {
			throw new IllegalStateException("File operation failed while creating unused folder", e);
		}

	}

	private void identifyUsedTargets(Path file) {
		try {
			Files.list(file).forEach(current -> {
				try {
					usedOriginalPaths.add(current.toRealPath());
				} catch (IOException e) {
					throw new IllegalStateException("Could not resolve real path for file " + current, e);
				}
			});
		} catch (IOException e) {
			throw new IllegalStateException("Could not files for " + file, e);
		}
	}

}
