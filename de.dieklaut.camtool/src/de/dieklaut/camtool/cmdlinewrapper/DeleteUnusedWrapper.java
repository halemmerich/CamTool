package de.dieklaut.camtool.cmdlinewrapper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.operations.DeleteUnused;
import de.dieklaut.camtool.operations.Operation;

public class DeleteUnusedWrapper extends AbstractWrapper {

	private static final String OPT_NO_AUTO_UPDATE_SHORT = "n";
	private static final String OPT_NO_AUTO_UPDATE = "noAutoUpdate";

	@Override
	public Options getOptions() {
		Options options = super.getOptions();
		options.addOption(Option.builder(OPT_NO_AUTO_UPDATE_SHORT).longOpt(OPT_NO_AUTO_UPDATE).desc("Automatically updates the unused folder before deletion").build());
		return options;
	}

	@Override
	public Operation getOperation(CommandLine cmdLine) {
		DeleteUnused deleteUnused = new DeleteUnused();
		if (cmdLine.hasOption(OPT_NO_AUTO_UPDATE)) {
			deleteUnused.setAutoUpdateBeforeDeletion(false);
		}
		return deleteUnused;
	}

	@Override
	public String getHelp() {
		return "This deletes all files pointed to from the unused folder in original and timeline folders";
	}

}
