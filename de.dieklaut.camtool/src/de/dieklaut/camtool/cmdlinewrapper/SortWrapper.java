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

	@Override
	public Options getOptions() {
		return new Options().addOption(Option.builder(OPT_NAME_SHORT).longOpt(OPT_NAME).desc("Sets the name for the sorting").build());
	}

	@Override
	public Operation getOperation(CommandLine cmdLine) {
		Sort sort = new Sort();
		if (cmdLine.hasOption(OPT_NAME)) {
			sort.setName(cmdLine.getOptionValue(OPT_NAME));
		}
		return sort;
	}

	@Override
	public String getHelp() {
		return "This operation creates a new sorting out of the contents of the " + Constants.FOLDER_TIMELINE + "folder.";
	}

}
