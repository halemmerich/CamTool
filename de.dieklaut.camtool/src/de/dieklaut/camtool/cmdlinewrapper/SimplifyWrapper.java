package de.dieklaut.camtool.cmdlinewrapper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.operations.Operation;
import de.dieklaut.camtool.operations.Simplify;

public class SimplifyWrapper extends AbstractWrapper {

	private static final String OPT_NAME_SHORT = "n";
	private static final String OPT_NAME = "name";
	private Sorter sorter;
	
	public SimplifyWrapper(Sorter sorter) {
		this.sorter = sorter;
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions().addOption(Option.builder(OPT_NAME_SHORT).longOpt(OPT_NAME).desc("Sets the name for the sorting").hasArg().build());
		return options;
	}

	@Override
	public Operation getOperation(CommandLine cmdLine) {
		Simplify simplify = new Simplify(sorter);
		if (cmdLine.hasOption(OPT_NAME)) {
			simplify.setSortingName(cmdLine.getOptionValue(OPT_NAME));
		}
		return simplify;
	}

	@Override
	public String getHelp() {
		return "This simplifies the folder structure of sorting";
	}

}
