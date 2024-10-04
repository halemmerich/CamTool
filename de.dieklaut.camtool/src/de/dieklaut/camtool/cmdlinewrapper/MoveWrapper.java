package de.dieklaut.camtool.cmdlinewrapper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.SortingHelper;
import de.dieklaut.camtool.operations.Move;
import de.dieklaut.camtool.operations.Operation;
import de.dieklaut.camtool.util.FileUtils;

public class MoveWrapper extends AbstractWrapper {

	private static final String OPT_NAME_SHORT = "n";
	private static final String OPT_NAME = "name";

	private static final String OPT_REGEX_SHORT = "r";
	private static final String OPT_REGEX = "regex";

	private static final String OPT_COMBINE_SHORT = "c";
	private static final String OPT_COMBINE = "combine";
	
	private Sorter sorter;
	
	public MoveWrapper(Sorter sorter) {
		this.sorter = sorter;
	}

	@Override
	public Options getOptions() {
		Options options = super.getOptions().addOption(Option.builder(OPT_NAME_SHORT).longOpt(OPT_NAME).desc("Sets the name for the sorting").hasArg().build());
		options.addOption(Option.builder(OPT_COMBINE_SHORT).longOpt(OPT_COMBINE).desc("Combine groups given into a new multi group with timestamp of the first").build());
		options.addOption(Option.builder(OPT_REGEX_SHORT).longOpt(OPT_REGEX).desc("Identify groups to move by regex instead of arguments").hasArg().build());
		return options;
	}

	@Override
	public Operation getOperation(CommandLine cmdLine, Path workingDir) {		
		if (cmdLine.getArgList().size() > 0 || cmdLine.hasOption(OPT_COMBINE)) {
			Path target = null;
			int numberOfGroupsToMove = 0;
			
			//identify target for the moving operation
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

			Move move = new Move(sorter);
			if (cmdLine.hasOption(OPT_NAME)) {
				String optionValue = cmdLine.getOptionValue(OPT_NAME);
				move.setSortingName(optionValue);
			} else {
				move.setSortingName(SortingHelper.detectSortingFromDir(workingDir));
			}
			move.setTargetPath(target);
			
			if (numberOfGroupsToMove > 0) {
				if (cmdLine.hasOption(OPT_REGEX)) {
					throw new IllegalArgumentException("Use either regex or a list of group identifiers");
				}
				List<String> identifiers = new ArrayList<>();
				for (int i = 0; i < numberOfGroupsToMove; i++) {
					identifiers.add(cmdLine.getArgList().get(i));
				}
				move.setIdentifiers(identifiers);
			} else {
				if (cmdLine.hasOption(OPT_REGEX)) {
					move.setRegex(cmdLine.getOptionValue(OPT_REGEX));
				}
			}
			return move;
		}
		throw new IllegalArgumentException("At least a target must be given as an argument");
	}

	@Override
	public String getHelp() {
		return "This moves groups around";
	}
	
	@Override
	public String getUsage() {
		return super.getUsage() + " <groups to be moved by path or name ...> <target group>";
	}

}
