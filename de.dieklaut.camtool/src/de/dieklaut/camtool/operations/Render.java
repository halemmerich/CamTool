package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.SortingHelper;
import de.dieklaut.camtool.renderjob.RenderJob;
import de.dieklaut.camtool.util.FileUtils;

/**
 * Performs all steps to create a {@link RenderJob} from a sorting.
 * 
 * @author mboonk
 *
 */
public class Render extends AbstractOperation {

	String sortingName = Constants.DEFAULT_SORTING_NAME;

	private Sorter sorter;

	private String groupName;

	private boolean force;

	public Render(Sorter sorter) {
		this.sorter = sorter;
	}

	public void setSortingName(String sortingName) {
		this.sortingName = sortingName;
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
			destination_direct = Files.createDirectories(results_sorting);
		} catch (IOException e) {
			throw new IllegalStateException("Could not create direct result folder", e);
		}

		Properties sourceState = new Properties();

		Path sourceStatePath = destination_direct.resolve(Constants.FILE_NAME_SOURCESTATE);
		loadSourceState(sourceState, sourceStatePath);

		if (groupName != null) {
			Group group = SortingHelper.findGroupByName(groups, groupName);
			groups = new HashSet<>();
			groups.add(group);
		}

		Map<RenderJob, String> renderJobToGroupName = new HashMap<>();
		Map<RenderJob, String> renderJobToChecksum = new HashMap<>();

		Set<Path> rendered = new HashSet<>();
		rendered.add(sourceStatePath);

		boolean predictionFailed = false;
		boolean errorDuringJobCreations = false;

		for (Group group : groups) {
			String newChecksum = hasChanges(group, sourceState);
			RenderJob renderJob = group.getRenderJob();
			try {
				Collection<? extends Path> predictedResults = renderJob.getPredictedResults(destination_direct);
				predictionFailed |= predictedResults == null;

				if (force || newChecksum != null || predictionFailed) {
					if (force) {
						newChecksum = FileUtils.getChecksum(group.getAllFiles());
					}
					renderJobToGroupName.put(renderJob, group.getName());
					renderJobToChecksum.put(renderJob, newChecksum);
				} else {
					rendered.addAll(predictedResults);
				}
			} catch (IOException e) {
				Logger.log("Render job creation failed", e);
				errorDuringJobCreations = true;
			}
		}

		for (RenderJob job : renderJobToGroupName.keySet()) {
			try {
				sourceState.setProperty(renderJobToGroupName.get(job), renderJobToChecksum.get(job));
				rendered.addAll(job.store(destination_direct));
				sourceState.store(Files.newOutputStream(sourceStatePath),
						"Last update on " + Calendar.getInstance().getTime());
			} catch (IOException e) {
				Logger.log("Failed to execute render job " + job, e, Level.WARNING);
			}
		}

		if (!errorDuringJobCreations) {
			try {
				FileUtils.deleteEverythingBut(destination_direct, rendered);
			} catch (IOException e) {
				Logger.log("Failed to clean old results after rendering", e, Level.WARNING);
			}
		}
	}

	public static void loadSourceState(Properties sourceState, Path sourceStatePath) {
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

	public static String hasChanges(Group group, Properties sourceState) {
		String checksum = FileUtils.getChecksum(group.getAllFiles());
		if (sourceState.containsKey(group.getName()) && sourceState.getProperty(group.getName()).equals(checksum)) {
			return null;
		}
		return checksum;
	}

	public static String hasChanges(Path path, Properties sourceState) {
		String checksum = FileUtils.getChecksum(Arrays.asList(new Path[] { path }));
		if (sourceState.containsKey(path.toString()) && sourceState.getProperty(path.toString()).equals(checksum)) {
			return null;
		}
		return checksum;
	}

	public void setNameOfGroup(String group) {
		this.groupName = group;
	}

	public void setForce(boolean hasOption) {
		this.force = true;
	}

}
