package de.dieklaut.camtool.cmdlinewrapper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.operations.Operation;

public interface OperationWrapper {
	
	public Options getOptions();

	/**
	 * Constructs an {@link Operation} from command line options.
	 * @param cmdLine
	 * @return
	 */
	public Operation getOperation(CommandLine cmdLine);

	public String getName();
	
	public String getHelp();
}
