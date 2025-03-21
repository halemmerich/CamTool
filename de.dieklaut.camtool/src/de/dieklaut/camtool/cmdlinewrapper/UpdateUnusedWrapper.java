package de.dieklaut.camtool.cmdlinewrapper;

import java.nio.file.Path;

import org.apache.commons.cli.CommandLine;

import de.dieklaut.camtool.operations.Operation;
import de.dieklaut.camtool.operations.UpdateUnused;

public class UpdateUnusedWrapper extends AbstractWrapper {

	@Override
	public Operation getOperation(CommandLine cmdLine, Path workingDir) {
		return new UpdateUnused();
	}

	@Override
	public String getHelp() {
		return "Updates the folder containing links to unused files";
	}

}
