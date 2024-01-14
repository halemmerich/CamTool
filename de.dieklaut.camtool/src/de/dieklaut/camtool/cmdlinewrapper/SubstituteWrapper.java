package de.dieklaut.camtool.cmdlinewrapper;

import java.nio.file.Path;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.operations.Operation;
import de.dieklaut.camtool.operations.Substitute;

public class SubstituteWrapper extends AbstractWrapper {

	private static final String OPT_NAME_SHORT = "n";
	private static final String OPT_NAME = "name";

	private static final String OPT_GROUP_SHORT = "g";
	private static final String OPT_GROUP = "group";

	private static final String OPT_SWITCH_SHORT = "x";
	private static final String OPT_SWITCH = "switch";

	private static final String OPT_SUBSTITUTION_SHORT = "s";
	private static final String OPT_SUBSTITUTION = "substitution";
	
	public SubstituteWrapper() {
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions().addOption(Option.builder(OPT_NAME_SHORT).longOpt(OPT_NAME).desc("Sets the name for the sorting").hasArg().build());
		options.addOption(Option.builder(OPT_GROUP_SHORT).longOpt(OPT_GROUP).desc("Sets the group to be moved").hasArg().build());
		options.addOption(Option.builder(OPT_SWITCH_SHORT).longOpt(OPT_SWITCH).desc("Switch between internal and external substitution").build());
		options.addOption(Option.builder(OPT_SUBSTITUTION_SHORT).longOpt(OPT_SUBSTITUTION).desc("Sets the files to be contained in the substitution file").hasArgs().build());
		return options;
	}

	@Override
	public Operation getOperation(CommandLine cmdLine, Path workingDir) {
		Substitute substitute = new Substitute();
		if (cmdLine.hasOption(OPT_NAME)) {
			String optionValue = cmdLine.getOptionValue(OPT_NAME);
			substitute.setSortingName(optionValue);
		}
		if (cmdLine.hasOption(OPT_GROUP)) {
			substitute.setNameOfGroup(cmdLine.getOptionValue(OPT_GROUP));
		} else {			
			substitute.setNameOfGroup(workingDir.getFileName().toString());
		}
		if (cmdLine.hasOption(OPT_SWITCH)) {
			substitute.setSwitch(true);
		}
		if (cmdLine.hasOption(OPT_SUBSTITUTION)) {
			substitute.setSubstitutions(cmdLine.getOptionValues(OPT_SUBSTITUTION));
		}
		return substitute;
	}

	@Override
	public String getHelp() {
		return "This substitutes a groups contents with other files for rendering";
	}
	
	@Override
	public String getUsage() {
		return super.getUsage() + " <groups to be moved ...> <target group>";
	}

}
