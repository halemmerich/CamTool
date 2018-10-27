package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.FileTypeHelper;
import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.SortingHelper;
import de.dieklaut.camtool.renderjob.RenderJob;
import de.dieklaut.camtool.util.CopyImageResizer;
import de.dieklaut.camtool.util.CopyVideoResizer;
import de.dieklaut.camtool.util.ImageResizer;
import de.dieklaut.camtool.util.VideoResizer;

/**
 * Performs all steps to create a {@link RenderJob} from a sorting.
 * 
 * @author mboonk
 *
 */
public class Render extends AbstractOperation {

	String sortingName = Constants.DEFAULT_SORTING_NAME;

	private ImageResizer imageResizer = new CopyImageResizer();
	private VideoResizer videoResizer = new CopyVideoResizer();
	private int qualityFull = 98;
	private int qualityMedium = 90;
	private int qualitySmall = 85;
	private int qualityVideoFull = 90;
	private int qualityVideoMedium = 80;
	private int qualityVideoSmall = 70;
	private int maxDimensionSmall = 2000;
	private int maxDimensionMedium = 3000;
	private int maxDimensionVideoMedium = 1920;
	private int maxDimensionVideoSmall = 1280;

	private Sorter sorter;

	private String group;
	
	public Render(Sorter sorter) {
		this.sorter = sorter;
	}

	public void setSortingName(String sortingName) {
		this.sortingName = sortingName;
	}
	
	public void setImageResizer(ImageResizer imageResizer) {
		this.imageResizer = imageResizer;
	}

	public void setVideoResizer(VideoResizer videoResizer) {
		this.videoResizer = videoResizer;
	}

	public void setQualityMedium(int qualityMedium) {
		this.qualityMedium = qualityMedium;
	}

	public void setQualitySmall(int qualitySmall) {
		this.qualitySmall = qualitySmall;
	}

	public void setQualityVideoMedium(int qualityVideoMedium) {
		this.qualityVideoMedium = qualityVideoMedium;
	}

	public void setQualityVideoSmall(int qualityVideoSmall) {
		this.qualityVideoSmall = qualityVideoSmall;
	}

	public void setMaxDimensionSmall(int maxDimensionSmall) {
		this.maxDimensionSmall = maxDimensionSmall;
	}

	public void setMaxDimensionMedium(int maxDimensionMedium) {
		this.maxDimensionMedium = maxDimensionMedium;
	}

	public void setMaxDimensionVideoMedium(int maxDimensionVideoMedium) {
		this.maxDimensionVideoMedium = maxDimensionVideoMedium;
	}

	public void setMaxDimensionVideoSmall(int maxDimensionVideoSmall) {
		this.maxDimensionVideoSmall = maxDimensionVideoSmall;
	}

	@Override
	public void perform(Context context) {
		Collection<Group> groups;
		try {
			groups = sorter.identifyGroups(context.getRoot().resolve(Constants.FOLDER_SORTED).resolve(sortingName));
		} catch (IOException e) {
			throw new IllegalStateException("Could not read groups", e);
		}

		Collection<RenderJob> renderJobs = new HashSet<>();
		if (group != null) {
			renderJobs.add(SortingHelper.findGroupToMove(groups, group).getRenderJob());
		} else {
			for (Group group : groups) {
				renderJobs.add(group.getRenderJob());
			}
		}

		Path results_sorting = context.getRoot().resolve(Constants.FOLDER_RESULTS).resolve(sortingName);
		Path destination_direct = results_sorting.resolve(Constants.RENDER_TYPE_DIRECT);
		Path destination_full = results_sorting.resolve(Constants.RENDER_TYPE_FULL);
		Path destination_medium = results_sorting.resolve(Constants.RENDER_TYPE_MEDIUM);
		Path destination_small = results_sorting.resolve(Constants.RENDER_TYPE_SMALL);

		try {
			Files.createDirectories(destination_direct);
			for (RenderJob job : renderJobs) {
				try {
					job.store(destination_direct);
				} catch (IOException e) {
					Logger.log("Failed to execute render job " + job, e, Level.WARNING);
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException("Creating full size results folder failed", e);
		}

		try {
			Files.createDirectories(destination_full);
			Files.list(destination_direct).forEach(file -> {
				if (!file.getFileName().toString().equals(Constants.SORTED_FILE_NAME)) {
					convertToFull(file, destination_full);
				}
			});
		} catch (IOException e) {
			throw new IllegalStateException("Creating full size results folder failed", e);
		}

		try {
			Files.createDirectories(destination_medium);
			Files.list(destination_full).forEach(file -> {
				if (!file.getFileName().toString().equals(Constants.SORTED_FILE_NAME)) {
					downsizeToMedium(file, destination_medium.resolve(file.getFileName()));
				}
			});
		} catch (IOException e) {
			throw new IllegalStateException("Creating medium size result files failed", e);
		}

		try {
			Files.createDirectories(destination_small);
			Files.list(destination_full).forEach(file -> {
				if (!file.getFileName().toString().equals(Constants.SORTED_FILE_NAME)) {
					downsizeToSmall(file, destination_small.resolve(file.getFileName()));
				}
			});
		} catch (IOException e) {
			throw new IllegalStateException("Creating small size result files failed", e);
		}
	}

	private void downsizeToSmall(Path file, Path destination_small) {
		if (FileTypeHelper.isVideoFile(file)) {
			videoResizer.resize(maxDimensionVideoSmall, file, destination_small, qualityVideoSmall);
		} else if (FileTypeHelper.isImageFile(file)) {
			imageResizer.resize(maxDimensionSmall, file, destination_small, qualitySmall);
		} else {
			try {
				Files.copy(file, destination_small);
			} catch (IOException e) {
				Logger.log("Could not fallback to copying for resizing to small", e, Level.WARNING);
			}
		}
	}

	private void downsizeToMedium(Path file, Path destination_medium) {
		if (FileTypeHelper.isVideoFile(file)) {
			videoResizer.resize(maxDimensionVideoMedium, file, destination_medium, qualityVideoMedium);
		} else if (FileTypeHelper.isImageFile(file)) {
			imageResizer.resize(maxDimensionMedium, file, destination_medium, qualityMedium);
		} else {
			try {
				Files.copy(file, destination_medium);
			} catch (IOException e) {
				Logger.log("Could not fallback to copying for resizing to medium", e, Level.WARNING);
			}
		}
	}

	private void convertToFull(Path file, Path destination) {
		if (FileTypeHelper.isVideoFile(file)) {
			videoResizer.resize(-1, file, destination.resolve(file.getFileName()), qualityVideoFull);
		} else if (FileTypeHelper.isImageFile(file)) {			
			imageResizer.resize(-1, file, destination.resolve(FileUtils.removeSuffix(file.getFileName().toString()) + ".jpg"), qualityFull);
		} else {
			try {
				Files.copy(file, destination);
			} catch (IOException e) {
				Logger.log("Could not fallback to copying for resizing to medium", e, Level.WARNING);
			}
		}
	}

	public void setNameOfGroup(String group) {
		this.group = group;
	}

}
