package de.dieklaut.camtool.cmdlinewrapper;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.ImagemagickResizer;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.operations.Operation;
import de.dieklaut.camtool.operations.Render;

public class RenderWrapper extends AbstractWrapper {
	
	private static final String OPT_NAME_SHORT = "n";
	private static final String OPT_NAME = "name";
	private Sorter sorter;
	
	public RenderWrapper(Sorter sorter) {
		this.sorter = sorter;
	}

	@Override
	public Options getOptions() {
		return super.getOptions().addOption(Option.builder(OPT_NAME_SHORT).longOpt(OPT_NAME).desc("Sets the name for the sorting").hasArg().build());
	}

	@Override
	public Operation getOperation(CommandLine cmdLine) {
		Render render = new Render(sorter);
		render.setImageResizer(new ImagemagickResizer());
		if (cmdLine.hasOption(OPT_NAME)) {
			render.setSortingName(cmdLine.getOptionValue(OPT_NAME));
		}
		return render;
	}

	@Override
	public String getHelp() {
		return "Renders a sorting to create the result files";
	}

}
