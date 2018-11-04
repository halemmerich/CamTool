package de.dieklaut.camtool.cmdlinewrapper;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.operations.Export;
import de.dieklaut.camtool.operations.Operation;

public class ExportWrapper extends AbstractWrapper {

	private static final String OPT_NAME_SHORT = "n";
	private static final String OPT_NAME = "name";
	private static final String OPT_TYPE_SHORT = "t";
	private static final String OPT_TYPE = "type";
	private static final String OPT_DESTINATION_SHORT = "d";
	private static final String OPT_DESTINATION = "destination";

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		options.addOption(Option.builder(OPT_NAME_SHORT).longOpt(OPT_NAME).desc("The name for the sorting that should be exported").hasArg().build());
		options.addOption(Option.builder(OPT_TYPE_SHORT).longOpt(OPT_TYPE).desc("The type of the results that should be exported").hasArg().build());
		options.addOption(Option.builder(OPT_DESTINATION_SHORT).longOpt(OPT_DESTINATION).desc("The destination folder for the export").hasArg().build());
		return options;
	}

	@Override
	public Operation getOperation(CommandLine cmdLine, Path workingDir) {
		Export export = new Export();
		if (cmdLine.hasOption(OPT_NAME)) {
			export.setName(cmdLine.getOptionValue(OPT_NAME));
		}
		if (cmdLine.hasOption(OPT_TYPE)) {
			export.setType(cmdLine.getOptionValue(OPT_TYPE));
		}
		if (cmdLine.hasOption(OPT_DESTINATION)) {
			export.setDestination(Paths.get(cmdLine.getOptionValue(OPT_DESTINATION)));
		}
		return export;
	}

	@Override
	public String getHelp() {
		return "Copies the results to a destination folder";
	}

}
