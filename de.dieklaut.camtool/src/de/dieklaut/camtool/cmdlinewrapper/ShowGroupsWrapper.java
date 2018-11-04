package de.dieklaut.camtool.cmdlinewrapper;

import java.nio.file.Path;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.UserInterface;
import de.dieklaut.camtool.operations.Operation;
import de.dieklaut.camtool.operations.ShowGroups;

public class ShowGroupsWrapper extends AbstractWrapper {

	private static final String OPT_NAME_SHORT = "n";
	private static final String OPT_NAME = "name";
	private static final String OPT_VERBOSITY_SHORT = "v";
	private static final String OPT_VERBOSITY = "verbosity";
	private Sorter sorter;
	private UserInterface ui;
	
	public ShowGroupsWrapper(Sorter sorter, UserInterface ui) {
		this.sorter = sorter;
		this.ui = ui;
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions().addOption(Option.builder(OPT_NAME_SHORT).longOpt(OPT_NAME).desc("Sets the name for the sorting").hasArg().build());
		options.addOption(Option.builder(OPT_VERBOSITY_SHORT).longOpt(OPT_VERBOSITY).desc("Sets the verbosity level").hasArg().build());
		return options;
	}

	@Override
	public Operation getOperation(CommandLine cmdLine, Path workingDir) {
		ShowGroups showGroups = new ShowGroups(sorter, ui);
		if (cmdLine.hasOption(OPT_NAME)) {
			showGroups.setSortingName(cmdLine.getOptionValue(OPT_NAME));
		}
		if (cmdLine.hasOption(OPT_VERBOSITY)) {
			showGroups.setVerbosity(Integer.parseInt(cmdLine.getOptionValue(OPT_VERBOSITY)));
		}
		return showGroups;
	}

	@Override
	public String getHelp() {
		return "This shows the groups in a sorting";
	}

}
