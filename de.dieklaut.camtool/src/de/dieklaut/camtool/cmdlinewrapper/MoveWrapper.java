package de.dieklaut.camtool.cmdlinewrapper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.SortingHelper;
import de.dieklaut.camtool.operations.Move;
import de.dieklaut.camtool.operations.MultiOperation;
import de.dieklaut.camtool.operations.Operation;
import de.dieklaut.camtool.util.FileUtils;

public class MoveWrapper extends AbstractWrapper {

	private static final String OPT_NAME_SHORT = "n";
	private static final String OPT_NAME = "name";

	private static final String OPT_TARGET_SHORT = "t";
	private static final String OPT_TARGET = "target";

	private static final String OPT_GROUP_SHORT = "g";
	private static final String OPT_GROUP = "group";

	private static final String OPT_COMBINE_SHORT = "c";
	private static final String OPT_COMBINE = "combine";
	
	private Sorter sorter;
	
	public MoveWrapper(Sorter sorter) {
		this.sorter = sorter;
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions().addOption(Option.builder(OPT_NAME_SHORT).longOpt(OPT_NAME).desc("Sets the name for the sorting").hasArg().build());
		options.addOption(Option.builder(OPT_GROUP_SHORT).longOpt(OPT_GROUP).desc("Sets the group to be moved").hasArg().build());
		options.addOption(Option.builder(OPT_TARGET_SHORT).longOpt(OPT_TARGET).desc("Sets the target folder, relative to the groups containing folder").hasArg().build());
		options.addOption(Option.builder(OPT_COMBINE_SHORT).longOpt(OPT_COMBINE).desc("Combine groups identified with given paths").build());
		return options;
	}

	@Override
	public Operation getOperation(CommandLine cmdLine, Path workingDir) {		
		if (cmdLine.getArgList().size() > 0) {
			List<Operation> ops = new LinkedList<>();
			
			Path target = null;
			int numberOfGroupsToMove = 0;
			
			if (cmdLine.hasOption(OPT_COMBINE)) {
				String firstFileName = Paths.get(cmdLine.getArgList().stream().findFirst().get()).getFileName().toString();
				String timestamp = FileUtils.getTimestampPortion(firstFileName);
				target = workingDir.resolve(timestamp + "_multi");
				numberOfGroupsToMove = cmdLine.getArgList().size();
			} else {
				Path givenPath = Paths.get(cmdLine.getArgList().get(cmdLine.getArgList().size() - 1) + "/");
				if (givenPath.isAbsolute()) {
					target = givenPath;
				} else {
					target = workingDir.resolve(givenPath);
				}
				numberOfGroupsToMove = cmdLine.getArgList().size() - 1;
			}

			String sortingNameFromWorkDir = SortingHelper.detectSortingFromDir(workingDir);
			
			for (int i = 0; i < numberOfGroupsToMove; i++) {
				Move move = new Move(sorter);
				if (cmdLine.hasOption(OPT_NAME)) {
					String optionValue = cmdLine.getOptionValue(OPT_NAME);
					move.setSortingName(optionValue);
				}

				move.setPathOfGroup(workingDir.resolve(cmdLine.getArgList().get(i)));
				move.setTargetPath(target);
				move.setSortingName(sortingNameFromWorkDir);
				ops.add(move);
			}
			return new MultiOperation(ops.toArray(new Operation[ops.size()]));
		} else {
			Move move = new Move(sorter);
			if (cmdLine.hasOption(OPT_NAME)) {
				String optionValue = cmdLine.getOptionValue(OPT_NAME);
				move.setSortingName(optionValue);
			}
			if (cmdLine.hasOption(OPT_TARGET)) {
				String optionValue = cmdLine.getOptionValue(OPT_TARGET);
				move.setTargetPath(Paths.get("./" + optionValue + "/"));
			}
			if (cmdLine.hasOption(OPT_GROUP)) {
				move.setNameOfGroup(cmdLine.getOptionValue(OPT_GROUP));
			}
			return move;
		}
		

		
	}

	@Override
	public String getHelp() {
		return "This moves groups around";
	}
	
	@Override
	public String getUsage() {
		return super.getUsage() + " <groups to be moved ...> <target group>";
	}

}
