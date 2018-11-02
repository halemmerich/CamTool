package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

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
import de.dieklaut.camtool.util.FileUtils;
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

	private String groupName;
	
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

		Path results_sorting = context.getRoot().resolve(Constants.FOLDER_RESULTS).resolve(sortingName);

		Path destination_direct;
		try {
			destination_direct = Files.createDirectories(results_sorting.resolve(Constants.RENDER_TYPE_DIRECT));
		} catch (IOException e) {
			throw new IllegalStateException("Could not create direct result folder", e);
		}
		
		Properties sourceState = new Properties();
		
		Path sourceStatePath = destination_direct.resolve(Constants.FILE_NAME_SOURCESTATE);
		loadSourceState(sourceState, sourceStatePath);
		
		if (groupName != null) {
			Group group = SortingHelper.findGroupToMove(groups, groupName);
			groups = new HashSet<>();
			groups.add(group);
		}

		Map<RenderJob, String> renderJobToGroupName = new HashMap<>();
		Map<RenderJob, String> renderJobToChecksum = new HashMap<>();
		
		for (Group group : groups) {
			String newChecksum = hasChanges(group, sourceState);
			if (newChecksum != null) {
				RenderJob renderJob = group.getRenderJob();
				renderJobToGroupName.put(renderJob, group.getName());
				renderJobToChecksum.put(renderJob, newChecksum);
			}
		}
		
		Path destination_full = results_sorting.resolve(Constants.RENDER_TYPE_FULL);
		Path destination_medium = results_sorting.resolve(Constants.RENDER_TYPE_MEDIUM);
		Path destination_small = results_sorting.resolve(Constants.RENDER_TYPE_SMALL);
		
		for (RenderJob job : renderJobToGroupName.keySet()) {
			try {
				sourceState.setProperty(renderJobToGroupName.get(job), renderJobToChecksum.get(job));
				job.store(destination_direct);
				sourceState.store(Files.newOutputStream(sourceStatePath), "Last update on " + Calendar.getInstance().getTime());
			} catch (IOException e) {
				Logger.log("Failed to execute render job " + job, e, Level.WARNING);
			}
		}

		try {
			Files.createDirectories(destination_full);
			Properties sourceStateFull = new Properties();
			Path sourceStatePathFull = destination_full.resolve(Constants.FILE_NAME_SOURCESTATE);
			loadSourceState(sourceStateFull, sourceStatePathFull);
			Files.list(destination_direct).forEach(file -> {
				if (!file.getFileName().toString().equals(Constants.SORTED_FILE_NAME) && !file.getFileName().toString().equals(Constants.FILE_NAME_SOURCESTATE)) {
					String checksum = hasChanges(file, sourceStateFull);
					if (checksum != null && convertToFull(file, destination_full)) {
						sourceStateFull.setProperty(file.toString(), checksum);
						try {
							sourceStateFull.store(Files.newOutputStream(sourceStatePathFull), "Last update on " + Calendar.getInstance().getTime());
						} catch (IOException e) {
							Logger.log("Error during save of source state file for full size", e);
						}
					}
				}
			});
		} catch (IOException e) {
			throw new IllegalStateException("Creating full size results folder failed", e);
		}

		try {
			Files.createDirectories(destination_medium);
			Properties sourceStateMedium = new Properties();
			Path sourceStatePathMedium = destination_medium.resolve(Constants.FILE_NAME_SOURCESTATE);
			loadSourceState(sourceStateMedium, sourceStatePathMedium);
			Files.list(destination_full).forEach(file -> {
				if (!file.getFileName().toString().equals(Constants.SORTED_FILE_NAME) && !file.getFileName().toString().equals(Constants.FILE_NAME_SOURCESTATE)) {
					String checksum = hasChanges(file, sourceStateMedium);
					if (checksum != null && downsizeToMedium(file, destination_medium.resolve(file.getFileName()))) {
						sourceStateMedium.setProperty(file.toString(), checksum);
						try {
							sourceStateMedium.store(Files.newOutputStream(sourceStatePathMedium), "Last update on " + Calendar.getInstance().getTime());
						} catch (IOException e) {
							Logger.log("Error during save of source state file for medium size", e);
						}
					}
				}
			});
		} catch (IOException e) {
			throw new IllegalStateException("Creating medium size result files failed", e);
		}

		try {
			Files.createDirectories(destination_small);
			Properties sourceStateSmall = new Properties();
			Path sourceStatePathSmall = destination_small.resolve(Constants.FILE_NAME_SOURCESTATE);
			loadSourceState(sourceStateSmall, sourceStatePathSmall);
			Files.list(destination_full).forEach(file -> {
				if (!file.getFileName().toString().equals(Constants.SORTED_FILE_NAME) && !file.getFileName().toString().equals(Constants.FILE_NAME_SOURCESTATE)) {
					String checksum = hasChanges(file, sourceStateSmall);
					if (checksum != null && downsizeToSmall(file, destination_small.resolve(file.getFileName()))) {
						sourceStateSmall.setProperty(file.toString(), checksum);
						try {
							sourceStateSmall.store(Files.newOutputStream(sourceStatePathSmall), "Last update on " + Calendar.getInstance().getTime());
						} catch (IOException e) {
							Logger.log("Error during save of source state file for medium size", e);
						}
					}
				}
			});
		} catch (IOException e) {
			throw new IllegalStateException("Creating small size result files failed", e);
		}
	}

	private void loadSourceState(Properties sourceState, Path sourceStatePath) {
		if (Files.exists(sourceStatePath)) {
			try {
				sourceState.load(Files.newInputStream(sourceStatePath));
			} catch (IOException e) {
				Logger.log("Could not load source state file state for rendering", e);
			}
		} else {
			try {
				Files.createFile(sourceStatePath);
			} catch (IOException e) {
				Logger.log("Could not create source state file state for rendering", e);
			}
		}
	}

	private String hasChanges(Group group, Properties sourceState) {
		String checksum = FileUtils.getChecksum(group.getAllFiles());
		if (sourceState.containsKey(group.getName()) && sourceState.getProperty(group.getName()).equals(checksum)) {
			return null;
		}
		return checksum;
	}

	private String hasChanges(Path path, Properties sourceState) {
		String checksum = FileUtils.getChecksum(Arrays.asList(new Path [] {path}));
		if (sourceState.containsKey(path.toString()) && sourceState.getProperty(path.toString()).equals(checksum)) {
			return null;
		}
		return checksum;
	}

	private boolean downsizeToSmall(Path file, Path destination_small) {
		if (FileTypeHelper.isVideoFile(file)) {
			videoResizer.resize(maxDimensionVideoSmall, file, destination_small, qualityVideoSmall);
		} else if (FileTypeHelper.isImageFile(file)) {
			imageResizer.resize(maxDimensionSmall, file, destination_small, qualitySmall);
		} else {
			try {
				Files.copy(file, destination_small, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				Logger.log("Could not fallback to copying for resizing to small", e, Level.WARNING);
				return false;
			}
		}
		return true;
	}

	private boolean downsizeToMedium(Path file, Path destination_medium) {
		if (FileTypeHelper.isVideoFile(file)) {
			videoResizer.resize(maxDimensionVideoMedium, file, destination_medium, qualityVideoMedium);
		} else if (FileTypeHelper.isImageFile(file)) {
			imageResizer.resize(maxDimensionMedium, file, destination_medium, qualityMedium);
		} else {
			try {
				Files.copy(file, destination_medium, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				Logger.log("Could not fallback to copying for resizing to medium", e, Level.WARNING);
				return false;
			}
		}
		return true;
	}

	private boolean convertToFull(Path file, Path destination) {
		if (FileTypeHelper.isVideoFile(file)) {
			videoResizer.resize(-1, file, destination.resolve(file.getFileName()), qualityVideoFull);
		} else if (FileTypeHelper.isImageFile(file)) {			
			imageResizer.resize(-1, file, destination.resolve(FileUtils.removeSuffix(file.getFileName().toString()) + ".jpg"), qualityFull);
		} else {
			try {
				Files.copy(file, destination.resolve(file.getFileName()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				Logger.log("Could not fallback to copying for resizing to full", e, Level.WARNING);
				return false;
			}
		}
		return true;
	}

	public void setNameOfGroup(String group) {
		this.groupName = group;
	}

}
