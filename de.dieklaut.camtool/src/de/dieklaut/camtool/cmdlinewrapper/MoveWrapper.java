package de.dieklaut.camtool.cmdlinewrapper;

import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.operations.Move;
import de.dieklaut.camtool.operations.Operation;

public class MoveWrapper extends AbstractWrapper {

	private static final String OPT_NAME_SHORT = "n";
	private static final String OPT_NAME = "name";

	private static final String OPT_TARGET_SHORT = "t";
	private static final String OPT_TARGET = "target";

	private static final String OPT_GROUP_SHORT = "g";
	private static final String OPT_GROUP = "group";
	
	private Sorter sorter;
	
	public MoveWrapper(Sorter sorter) {
		this.sorter = sorter;
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions().addOption(Option.builder(OPT_NAME_SHORT).longOpt(OPT_NAME).desc("Sets the name for the sorting").hasArg().build());
		options.addOption(Option.builder(OPT_GROUP_SHORT).longOpt(OPT_GROUP).desc("Sets the group to be moved").hasArg().build());
		options.addOption(Option.builder(OPT_TARGET_SHORT).longOpt(OPT_TARGET).desc("Sets the target folder, relative to the groups containing folder").hasArg().build());
		return options;
	}

	@Override
	public Operation getOperation(CommandLine cmdLine) {
		Move move = new Move(sorter);
		if (cmdLine.hasOption(OPT_NAME)) {
			String optionValue = cmdLine.getOptionValue(OPT_NAME);
			move.setSortingName(optionValue);
		}
		if (cmdLine.hasOption(OPT_TARGET)) {
			String optionValue = cmdLine.getOptionValue(OPT_TARGET);
			move.setTargetPath(Paths.get("./" + optionValue + "/"));
		}
		if (cmdLine.hasOption(OPT_GROUP)) {
			move.setNameOfGroup(cmdLine.getOptionValue(OPT_GROUP));
		}
		return move;
	}

	@Override
	public String getHelp() {
		return "This moves a group around";
	}

}
