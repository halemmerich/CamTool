package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.MultiGroup;
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
		Map<RenderJob, String> renderJobToNamePrefix = new HashMap<>();

		Set<Path> rendered = new HashSet<>();
		rendered.add(sourceStatePath);

		boolean predictionFailed = false;
		boolean errorDuringJobCreations = false;

		for (Group group : groups) {
			String newChecksum = hasChanges(group, sourceState);
			RenderJob renderJob = group.getRenderJob();
			try {
				Collection<Path> predictedResults = renderJob.getPredictedResults(destination_direct);
				predictionFailed |= predictedResults == null;

				
				Collection<Path> modifiedPaths = new HashSet<>();
				if (group instanceof MultiGroup) {
					if (predictedResults != null && predictedResults.size() == 1) {
						Path result = predictedResults.iterator().next();
						Path newPath = destination_direct.resolve(group.getName() + FileUtils.getSuffix(result));
						modifiedPaths.add(newPath);
						Logger.log("Modified predicted path of single Multigroup output " + result + " to " + newPath, Level.DEBUG);
					} else if (predictedResults != null && predictedResults.size() > 1) {
						Iterator<Path> it = predictedResults.iterator();
						while (it.hasNext()) {
							Path result = it.next();
							Path newPath = destination_direct.resolve(group.getName() + "_" + result.getFileName().toString());
							modifiedPaths.add(newPath);
							Logger.log("Modified predicted path of single Multigroup output " + result + " to " + newPath, Level.DEBUG);
						}
					}
					predictedResults = modifiedPaths;
				}
				
				boolean predictedFilesNotExisting = false;
				for (Path p : predictedResults) {
					if (!Files.exists(p)) {
						predictedFilesNotExisting = true;
						break;
					}
				}
				
				boolean executeForced = force || predictionFailed || predictedFilesNotExisting;
				
				if (executeForced || newChecksum != null) {
					if (executeForced) {
						newChecksum = FileUtils.getChecksum(group.getAllFiles());
					}
					renderJobToGroupName.put(renderJob, group.getName());
					renderJobToChecksum.put(renderJob, newChecksum);
					if (group instanceof MultiGroup) {
						renderJobToNamePrefix.put(renderJob, group.getName());
					}
				} else {
					rendered.addAll(predictedResults);
				}
				

				
			} catch (IOException e) {
				Logger.log("Render job creation failed", e);
				errorDuringJobCreations = true;
			}
		}

		Logger.log("Rendering " + renderJobToGroupName.size() + " jobs.", Level.INFO);
		
		for (RenderJob job : renderJobToGroupName.keySet()) {
			try {
				sourceState.setProperty(renderJobToGroupName.get(job), renderJobToChecksum.get(job));
				Set<Path> jobResult = job.store(destination_direct);
				
				if (renderJobToNamePrefix.containsKey(job) && jobResult.size() == 1) {
					Path result = jobResult.iterator().next();
					Path newPath = destination_direct.resolve(renderJobToNamePrefix.get(job) + FileUtils.getSuffix(result));
					Files.deleteIfExists(newPath);
					Files.move(result, newPath);
					jobResult.remove(result);
					jobResult.add(newPath);
					Logger.log("Renamed single Multigroup output " + result + " to " + newPath, Level.INFO);
				} else if (renderJobToNamePrefix.containsKey(job) && jobResult.size() > 1) {
					Set<Path> toBeRemoved = new HashSet<>();
					Set<Path> toBeAdded = new HashSet<>();
					Iterator<Path> it = jobResult.iterator();
					while (it.hasNext()) {
						Path result = it.next();
						Path newPath = destination_direct.resolve(renderJobToNamePrefix.get(job) + "_" + result.getFileName().toString());
						Files.deleteIfExists(newPath);
						Files.move(result, newPath);
						toBeRemoved.add(result);
						toBeAdded.add(newPath);
						Logger.log("Prefixed single Multigroup output " + result + " to " + newPath, Level.INFO);
					}
					jobResult.removeAll(toBeRemoved);
					jobResult.addAll(toBeAdded);
				}
				
				rendered.addAll(jobResult);
				sourceState.store(Files.newOutputStream(sourceStatePath),
						"Last update on " + Calendar.getInstance().getTime());
			} catch (IOException e) {
				Logger.log("Failed to execute render job " + job, e, Level.WARNING);
			}
		}
		
		Logger.log("Rendered finished: " + (rendered.size() - 1) + " resulting artifacts.", Level.INFO); //-1 because of .sourcestate file

		if (!errorDuringJobCreations && groupName == null) {
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
