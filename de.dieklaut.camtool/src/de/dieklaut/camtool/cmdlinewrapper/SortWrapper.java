package de.dieklaut.camtool.cmdlinewrapper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.operations.Operation;
import de.dieklaut.camtool.operations.Sort;

public class SortWrapper extends AbstractWrapper {

	private static final String OPT_NAME_SHORT = "n";
	private static final String OPT_NAME = "name";
	private static final String OPT_DETECT_BRACKETED_SHORT = "b";
	private static final String OPT_DETECT_SERIES_SHORT = "s";
	private static final String OPT_MOVE_ALL_GROUPS_SHORT = "a";
	private static final String OPT_MOVE_COLLECTIONS_SHORT = "c";
	private static final String OPT_DETECT_BRACKETED = "brackets";
	private static final String OPT_DETECT_SERIES = "series";
	private static final String OPT_MOVE_ALL_GROUPS = "groups";
	private static final String OPT_MOVE_COLLECTIONS = "collections";

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		options.addOption(Option.builder(OPT_NAME_SHORT).longOpt(OPT_NAME).desc("Sets the name for the sorting").hasArg().build());
		options.addOption(Option.builder(OPT_DETECT_BRACKETED_SHORT).longOpt(OPT_DETECT_BRACKETED).desc("Detect bracketed shots and put them into a collection").build());
		options.addOption(Option.builder(OPT_DETECT_SERIES_SHORT).longOpt(OPT_DETECT_SERIES).desc("Detect series shots and put them into a collection").build());
		options.addOption(Option.builder(OPT_MOVE_ALL_GROUPS_SHORT).longOpt(OPT_MOVE_ALL_GROUPS).desc("Move all detected groups into their own folder").build());
		options.addOption(Option.builder(OPT_MOVE_COLLECTIONS_SHORT).longOpt(OPT_MOVE_COLLECTIONS).desc("Move all detected collections into their own folder").build());
		return options;
	}

	@Override
	public Operation getOperation(CommandLine cmdLine) {
		Sort sort = new Sort();
		if (cmdLine.hasOption(OPT_NAME)) {
			sort.setName(cmdLine.getOptionValue(OPT_NAME));
		}
		if (cmdLine.hasOption(OPT_DETECT_BRACKETED)) {
			sort.setDetectBracketedShots(true);
		}
		if (cmdLine.hasOption(OPT_DETECT_SERIES)) {
			sort.setDetectSeries(true);
		}
		if (cmdLine.hasOption(OPT_MOVE_ALL_GROUPS)) {
			sort.setMoveAllGroupsToFolder(true);
		}
		if (cmdLine.hasOption(OPT_MOVE_COLLECTIONS)) {
			sort.setMoveCollectionsToFolder(true);
		}
		return sort;
	}

	@Override
	public String getHelp() {
		return "This operation creates a new sorting out of the contents of the " + Constants.FOLDER_TIMELINE + "folder.";
	}

}
