package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Properties;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.FileTypeHelper;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.util.CopyImageResizer;
import de.dieklaut.camtool.util.CopyVideoResizer;
import de.dieklaut.camtool.util.FileUtils;
import de.dieklaut.camtool.util.ImageResizer;
import de.dieklaut.camtool.util.VideoResizer;

/**
 * This exports the rendering results of a sorting into an arbitrary folder.
 * @author mboonk
 *
 */
public class Export extends AbstractOperation {
	
	private ImageResizer imageResizer = new CopyImageResizer();
	private VideoResizer videoResizer = new CopyVideoResizer();

	private String name = Constants.DEFAULT_SORTING_NAME;
	private String namePrefix = "";
	private ExportType type = ExportType.MEDIUM;
	
	private Path destination = null;
	private boolean preventCleanup = false;

	public void setName(String name) {
		this.name = name;
	}

	public void setNamePrefix(String namePrefix) {
		this.namePrefix = namePrefix;
	}

	public void setType(ExportType type) {
		this.type = type;
	}

	public void setDestination(Path destination) {
		this.destination = destination;
	}

	public void setImageResizer(ImageResizer imageResizer) {
		this.imageResizer = imageResizer;
	}

	public void setVideoResizer(VideoResizer videoResizer) {
		this.videoResizer = videoResizer;
	}

	@Override
	public void perform(Context context) {
		Path resultBase = context.getRoot().resolve(Constants.FOLDER_RESULTS);
		if (!namePrefix.isEmpty())
			resultBase = resultBase.resolve(Paths.get(namePrefix));
		Path resultFolder = resultBase.resolve(name);
		
		if (!Files.exists(resultFolder) || (Files.exists(resultFolder) && !Files.isDirectory(resultFolder))) {
			Logger.log("Result folder " + resultFolder + " does not exist or is not a directory.", Level.ERROR);
			return;
		}
		
		if (destination == null) {
			Path exportBase = context.getRoot().resolve(Constants.DEFAULT_EXPORT_NAME);
			if (!namePrefix.isEmpty())
				exportBase = exportBase.resolve(Paths.get(namePrefix));
			destination = exportBase.resolve(type.name().toLowerCase()).resolve(name);
		}
		
		if (!Files.exists(destination)) {
			try {
				Files.createDirectories(destination);
			} catch (IOException e) {
				Logger.log("Could not create export destination directory " + destination, e);
				return;
			}
		}
		
		
		try {
			Properties sourceStateFull = new Properties();
			Path sourceStatePathFull = destination.resolve(Constants.FILE_NAME_SOURCESTATE);
			Render.loadSourceState(sourceStateFull, sourceStatePathFull);
			Files.list(resultFolder).forEach(file -> {
				if (!file.getFileName().toString().equals(Constants.SORTED_FILE_NAME)
						&& !file.getFileName().toString().equals(Constants.FILE_NAME_SOURCESTATE)) {
					String checksum = Render.hasChanges(file, sourceStateFull);
					if (checksum != null && convertTo(file, destination, type)) {
						sourceStateFull.setProperty(file.toString(), checksum);
						try {
							sourceStateFull.store(Files.newOutputStream(sourceStatePathFull),
									"Last update on " + Calendar.getInstance().getTime());
						} catch (IOException e) {
							Logger.log("Error during save of source state file for full size", e);
						}
					}
				}
			});
			if (!preventCleanup) {
				FileUtils.deleteAllFilesNotExistingIn(resultFolder, destination, true);
			}
		} catch (IOException e) {
			throw new IllegalStateException("Creating full size results folder failed", e);
		}
	}

	private boolean convertTo(Path file, Path destination, ExportType type) {
		if (FileTypeHelper.isVideoFile(file)) {
			videoResizer.resize(type.maxVideoDimension, file, destination.resolve(FileUtils.removeSuffix(file.getFileName().toString()) + ".mkv"), type.videoQuality);
		} else if (FileTypeHelper.isImageFile(file) || FileTypeHelper.isVectorFile(file)) {
			imageResizer.resize(type.maxImageDimension, file,
					destination.resolve(FileUtils.removeSuffix(file.getFileName().toString()) + ".jpg"), type.imageQuality);
		} else {
			try {
				FileUtils.hardlinkOrCopy(file, destination.resolve(file.getFileName()));
			} catch (IOException e) {
				Logger.log("Could not fallback to copying for resizing", e, Level.WARNING);
				return false;
			}
		}
		return true;
	}

	public void setPreventCleanup(boolean preventCleanup) {
		this.preventCleanup  = preventCleanup;
	}

}
