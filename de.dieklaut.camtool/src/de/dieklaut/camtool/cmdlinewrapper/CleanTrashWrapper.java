package de.dieklaut.camtool.cmdlinewrapper;

import java.nio.file.Path;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.SortingHelper;
import de.dieklaut.camtool.operations.CleanTrash;
import de.dieklaut.camtool.operations.Operation;

public class CleanTrashWrapper extends AbstractWrapper {
	
	private static final String OPT_NAME_SHORT = "n";
	private static final String OPT_NAME = "name";
	private Sorter sorter;
	
	public CleanTrashWrapper(Sorter sorter) {
		this.sorter = sorter;
	}

	@Override
	public Options getOptions() {
		return super.getOptions().addOption(Option.builder(OPT_NAME_SHORT).longOpt(OPT_NAME).desc("Sets the name for the sorting").hasArg().build());
	}

	@Override
	public Operation getOperation(CommandLine cmdLine, Path workingDir) {
		CleanTrash clean = new CleanTrash(sorter);
		if (cmdLine.hasOption(OPT_NAME)) {
			clean.setName(cmdLine.getOptionValue(OPT_NAME));
		} else {
			clean.setName(SortingHelper.detectSortingFromDir(workingDir));
		}
		return clean;
	}

	@Override
	public String getHelp() {
		return "Deletes all groups that are marked as deleted";
	}

}
