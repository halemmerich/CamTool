package de.dieklaut.camtool.cmdlinewrapper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.operations.Operation;

/**
 * This wraps an {@link Operation} to be accessible from the commandline.
 * 
 * @author mboonk
 *
 */
public interface OperationWrapper {

	/**
	 * @return the {@link Options} that are available
	 */
	public Options getOptions();

	/**
	 * Constructs an {@link Operation} from command line options.
	 * 
	 * @param cmdLine
	 * @return
	 */
	public Operation getOperation(CommandLine cmdLine);

	/**
	 * @return the name which is to be used for command line access to this
	 *         operation
	 */
	public String getName();

	/**
	 * @return a textual description of the operation
	 */
	public String getHelp();
}
