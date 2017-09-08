package de.dieklaut.camtool.cmdlinewrapper;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public abstract class AbstractWrapper implements OperationWrapper{

	private static final String OPT_HELP_SHORT = "h";
	private static final String OPT_HELP = "help";

	@Override
	public Options getOptions() {
		return new Options().addOption(Option.builder(OPT_HELP_SHORT).longOpt(OPT_HELP).desc("Help for the " + getName().toLowerCase() + " Operation").build());
	}
	
	@Override
	public String getName() {
		return getClass().getSimpleName().replaceAll("Wrapper", "");
	}
	
}
