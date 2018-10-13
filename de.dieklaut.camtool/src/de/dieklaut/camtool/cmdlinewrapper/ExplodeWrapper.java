package de.dieklaut.camtool.cmdlinewrapper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.operations.Explode;
import de.dieklaut.camtool.operations.Operation;

public class ExplodeWrapper extends AbstractWrapper {

	private static final String OPT_NAME_SHORT = "n";
	private static final String OPT_NAME = "name";

	private static final String OPT_GROUP_SHORT = "g";
	private static final String OPT_GROUP = "group";
	
	private Sorter sorter;
	
	public ExplodeWrapper(Sorter sorter) {
		this.sorter = sorter;
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions().addOption(Option.builder(OPT_NAME_SHORT).longOpt(OPT_NAME).desc("Sets the name for the sorting").hasArg().build());
		options.addOption(Option.builder(OPT_GROUP_SHORT).longOpt(OPT_GROUP).desc("Sets the group to be moved").hasArg().build());
		return options;
	}

	@Override
	public Operation getOperation(CommandLine cmdLine) {
		Explode explode = new Explode(sorter);
		if (cmdLine.hasOption(OPT_NAME)) {
			String optionValue = cmdLine.getOptionValue(OPT_NAME);
			explode.setSortingName(optionValue);
		}
		if (cmdLine.hasOption(OPT_GROUP)) {
			explode.setGroupName(cmdLine.getOptionValue(OPT_GROUP));
		}
		return explode;
	}

	@Override
	public String getHelp() {
		return "This explodes a group into all of its sub groups if there are no extra files";
	}

}
