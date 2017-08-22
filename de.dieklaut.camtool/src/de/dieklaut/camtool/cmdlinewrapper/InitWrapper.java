package de.dieklaut.camtool.cmdlinewrapper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.operations.Init;
import de.dieklaut.camtool.operations.Operation;

public class InitWrapper extends AbstractWrapper {

	@Override
	public Options getOptions() {
		return super.getOptions();
	}

	@Override
	public Operation getOperation(CommandLine cmdLine) {
		return new Init();
	}

	@Override
	public String getHelp() {
		return "Initialize a camtool structure. This moves all files to the " + Constants.FOLDER_ORIGINAL
				+ " and write protects them.";
	}
}
