package de.dieklaut.camtool.cmdlinewrapper;

import java.nio.file.Path;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.operations.ModifyTimestamp;
import de.dieklaut.camtool.operations.Operation;

public class ModifyTimestampWrapper extends AbstractWrapper {

	private static final String OPT_DIFFERENCE_SHORT = "d";
	private static final String OPT_DIFFERENCE = "difference";

	private static final String OPT_TIMESTAMP_SHORT = "t";
	private static final String OPT_TIMESTAMP = "timestamp";

	private static final String OPT_REGEX_SHORT = "r";
	private static final String OPT_REGEX = "regex";

	public ModifyTimestampWrapper() {
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions().addOption(Option.builder(OPT_REGEX_SHORT).longOpt(OPT_REGEX)
				.desc("Set the regex to choose files to be shifted").hasArg().build());
		options.addOption(Option.builder(OPT_DIFFERENCE_SHORT).longOpt(OPT_DIFFERENCE)
				.desc("Time difference to be applied in milliseconds").hasArg().build());
		options.addOption(Option.builder(OPT_TIMESTAMP_SHORT).longOpt(OPT_TIMESTAMP)
				.desc("Timestamp to be applied in milliseconds").hasArg().build());
		return options;
	}

	@Override
	public Operation getOperation(CommandLine cmdLine, Path workingDir) {
			ModifyTimestamp ModifyTimestamp = new ModifyTimestamp();
			if (cmdLine.hasOption(OPT_REGEX)) {
				String optionValue = cmdLine.getOptionValue(OPT_REGEX);
				ModifyTimestamp.setRegex(optionValue);
			}
			if (cmdLine.hasOption(OPT_DIFFERENCE)) {
				String optionValue = cmdLine.getOptionValue(OPT_DIFFERENCE);
				ModifyTimestamp.setDifference(Long.parseLong(optionValue));
			}
			if (cmdLine.hasOption(OPT_TIMESTAMP)) {
				String optionValue = cmdLine.getOptionValue(OPT_TIMESTAMP);
				ModifyTimestamp.setTimestamp(optionValue);
			}
			return ModifyTimestamp;
	}

	@Override
	public String getName() {
		return "Timeshift";
	}

	@Override
	public String getHelp() {
		return "Timeshift help";
	}

	@Override
	public String getUsage() {
		return "Timeshift Usage";
	}

}
