package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.MultiGroup;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.UserInterface;

public class ShowGroups extends AbstractOperation {

	private Sorter sorter;
	private UserInterface ui;
	String sortingName = Constants.DEFAULT_SORTING_NAME;
	private int verbosity = 0;

	public ShowGroups(Sorter sorter, UserInterface ui) {
		this.sorter = sorter;
		this.ui = ui;
	}

	public void setSortingName(String sortingName) {
		this.sortingName = sortingName;
	}

	public void setVerbosity(int verbosity) {
		this.verbosity = verbosity;
	}

	@Override
	public void perform(Context context) {
		Collection<Group> groups;
		try {
			groups = sorter.identifyGroups(context.getRoot().resolve(Constants.FOLDER_SORTED).resolve(sortingName));
			StringBuilder result = new StringBuilder();
			show(groups, result, "");
			ui.show(result.toString());
		} catch (IOException e) {
			throw new IllegalStateException("Could not read groups", e);
		}
	}

	private void show(Collection<Group> groups, StringBuilder result, String indent) {
		for (Group g : groups) {
			show(g, result, indent);
		}
	}

	private void show(Group g, StringBuilder result, String indent) {
		if (verbosity == 0) {
			result.append(indent + g.getName() + System.lineSeparator());
		} else {
			result.append(indent + "Group:" + g.getName() + System.lineSeparator());
		}
		
		if (verbosity >= 1) {
			result.append(indent + " Type:              " + g.getType() + System.lineSeparator());
			result.append(indent + " Containing Folder: " + g.getContainingFolder() + System.lineSeparator());
			result.append(indent + " Timestamp:         " + g.getTimestamp() + System.lineSeparator());
			result.append(indent + " Duration:          " + g.getDuration() + System.lineSeparator());
		}

		if (g instanceof MultiGroup) {
			show(((MultiGroup) g).getGroups(), result, indent + "  ");
		} else {
			if (verbosity >= 2) {
				result.append(indent + " Files:");
				for (Path p : g.getAllFiles()) {
					result.append(indent + "  " + p + System.lineSeparator());
				}
			}
		}

		if (verbosity >= 1) {
			result.append(System.lineSeparator());
		}
	}

}
