package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.util.FileUtils;

/**
 * Finds all unused originals and deletes them. This can not be undone!
 * @author mboonk
 *
 */
public class DeleteUnused extends AbstractOperation {
	
	private boolean autoUpdateBeforeDeletion = true;
	
	public void setAutoUpdateBeforeDeletion(boolean autoUpdateBeforeDeletion) {
		this.autoUpdateBeforeDeletion = autoUpdateBeforeDeletion;
	}

	@Override
	public void perform(Context context) {
		if (autoUpdateBeforeDeletion) {
			new UpdateUnused().perform(context);
		}
		
		Path unusedFolder = context.getRoot().resolve(Constants.FOLDER_UNUSED);
		if (!Files.exists(unusedFolder)) {
			Logger.log("No unused folder, nothing to do", Level.WARNING);
			return;
		}
		try {
			Files.list(unusedFolder).forEach(file -> {
				try {
					FileUtils.deleteRecursive(file.toRealPath(), true);
					FileUtils.deleteRecursive(file, true);
				} catch (IOException e) {
					throw new IllegalStateException("Could not delete file " + file);
				}
			});
		} catch (IOException e) {
			throw new IllegalStateException("Listing the files of the unused folder failed");
		}
		
		try {
			Files.list(context.getTimeLine()).forEach(file -> {
				try {
					if (!Files.exists(context.getTimeLine().resolve(Files.readSymbolicLink(file)))) {
						FileUtils.deleteRecursive(file, false);
					}
				} catch (IOException e) {
					throw new IllegalStateException("Could not handle probably dangling link " + file);
				}
			});
		} catch (IOException e) {
			throw new IllegalStateException("Could not list files of timeline folder for cleanup of dangling links", e);
		}
	}

}
