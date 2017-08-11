package de.dieklaut.camtool.cmdlinewrapper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.operations.Operation;
import de.dieklaut.camtool.operations.Render;

public class RenderWrapper extends AbstractWrapper {
	
	private static final String OPT_NAME_SHORT = "n";
	private static final String OPT_NAME = "name";

	@Override
	public Options getOptions() {
		return new Options().addOption(Option.builder(OPT_NAME_SHORT).longOpt(OPT_NAME).desc("Sets the name for the sorting").build());
	}

	@Override
	public Operation getOperation(CommandLine cmdLine) {
		Render render = new Render();
		if (cmdLine.hasOption(OPT_NAME)) {
			render.setSortingName(cmdLine.getOptionValue(OPT_NAME));
		}
		return render;
	}

	@Override
	public String getHelp() {
		return "This renders a sorting to create the results";
	}

}
