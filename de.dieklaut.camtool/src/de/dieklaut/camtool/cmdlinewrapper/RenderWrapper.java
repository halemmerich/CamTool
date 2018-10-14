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
	private static final String OPT_GROUP_SHORT = "g";
	private static final String OPT_GROUP = "group";
	private Sorter sorter;
	
	public RenderWrapper(Sorter sorter) {
		this.sorter = sorter;
	}

	@Override
	public Options getOptions() {
		Options options =  super.getOptions().addOption(Option.builder(OPT_NAME_SHORT).longOpt(OPT_NAME).desc("Sets the name for the sorting").hasArg().build());
		options.addOption(Option.builder(OPT_GROUP_SHORT).longOpt(OPT_GROUP).desc("Sets the group to be moved").hasArg().build());
		return options;
	}

	@Override
	public Operation getOperation(CommandLine cmdLine) {
		Render render = new Render(sorter);
		render.setImageResizer(new ImagemagickResizer());
		if (cmdLine.hasOption(OPT_NAME)) {
			render.setSortingName(cmdLine.getOptionValue(OPT_NAME));
		}
		if (cmdLine.hasOption(OPT_GROUP)) {
			render.setNameOfGroup(cmdLine.getOptionValue(OPT_GROUP));
		}
		return render;
	}

	@Override
	public String getHelp() {
		return "Renders a sorting to create the result files";
	}

}
