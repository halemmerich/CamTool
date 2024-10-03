package de.dieklaut.camtool.cmdlinewrapper;

import java.nio.file.Path;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.operations.Operation;
import de.dieklaut.camtool.operations.Sort;

public class SortWrapper extends AbstractWrapper {

	private static final String OPT_NAME_SHORT = "n";
	private static final String OPT_NAME = "name";
	private static final String OPT_DETECT_SERIES_SHORT = "s";
	private static final String OPT_DETECT_SERIES_TIME_SHORT = "t";
	private static final String OPT_MINIMUM_NUMBER_OF_FILES_SHORT = "m";
	private static final String OPT_DETECT_SERIES = "series";
	private static final String OPT_DETECT_SERIES_TIME = "timediff";
	private static final String OPT_MINIMUM_NUMBER_OF_FILES = "min";
	private Sorter sorter;

	public SortWrapper(Sorter sorter) {
		this.sorter = sorter;
	}
	
	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		options.addOption(Option.builder(OPT_NAME_SHORT).longOpt(OPT_NAME).desc("Sets the name for the sorting").hasArg().build());
		options.addOption(Option.builder(OPT_DETECT_SERIES_SHORT).longOpt(OPT_DETECT_SERIES).desc("Detect series shots and put them into a collection").build());
		options.addOption(Option.builder(OPT_DETECT_SERIES_TIME_SHORT).longOpt(OPT_DETECT_SERIES_TIME).hasArg().desc("Time difference used for detecting series").build());
		options.addOption(Option.builder(OPT_MINIMUM_NUMBER_OF_FILES_SHORT).longOpt(OPT_MINIMUM_NUMBER_OF_FILES).hasArg().desc("Minimum number of files to be a series").build());
		return options;
	}

	@Override
	public Operation getOperation(CommandLine cmdLine, Path workingDir) {
		Sort sort = new Sort(sorter);
		if (cmdLine.hasOption(OPT_NAME)) {
			sort.setName(cmdLine.getOptionValue(OPT_NAME));
		}
		if (cmdLine.hasOption(OPT_DETECT_SERIES)) {
			sort.setDetectSeries(true);
		}
		if (cmdLine.hasOption(OPT_DETECT_SERIES_TIME)) {
			sort.setDetectSeriesTime(Integer.parseInt(cmdLine.getOptionValue(OPT_DETECT_SERIES_TIME)));
		}
		if (cmdLine.hasOption(OPT_MINIMUM_NUMBER_OF_FILES)) {
			sort.setMinimumNumberOfFiles(Integer.parseInt(cmdLine.getOptionValue(OPT_MINIMUM_NUMBER_OF_FILES)));
		}
		return sort;
	}

	@Override
	public String getHelp() {
		return "This operation creates a new sorting out of the contents of the " + Constants.FOLDER_TIMELINE + "folder";
	}

}
