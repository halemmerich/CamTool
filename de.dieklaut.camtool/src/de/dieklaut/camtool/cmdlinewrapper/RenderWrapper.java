package de.dieklaut.camtool.cmdlinewrapper;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.SortingHelper;
import de.dieklaut.camtool.operations.Operation;
import de.dieklaut.camtool.operations.Render;
import de.dieklaut.camtool.operations.RenderFilter;
import de.dieklaut.camtool.renderfilters.FileTypeFilter;
import de.dieklaut.camtool.renderfilters.Pp3IntFilter;
import de.dieklaut.camtool.renderfilters.Pp3MaxIntRenderFilter;
import de.dieklaut.camtool.renderfilters.Pp3MinIntRenderFilter;
import de.dieklaut.camtool.renderfilters.Pp3MinMaxIntRenderFilter;
import de.dieklaut.camtool.renderfilters.Pp3RenderFilter;

public class RenderWrapper extends AbstractWrapper {
	
	private static final String OPT_NAME_SHORT = "n";
	private static final String OPT_NAME = "name";
	private static final String OPT_GROUP_SHORT = "g";
	private static final String OPT_GROUP = "group";
	private static final String OPT_FORCE_SHORT = "f";
	private static final String OPT_FORCE = "force";
	private static final String OPT_RENDERFILTER_SHORT = "r";
	private static final String OPT_RENDERFILTER = "renderfilter";
	private static final String OPT_PREVENT_CLEANUP_SHORT = "p";
	private static final String OPT_PREVENT_CLEANUP = "prevent-cleanup";
	private Sorter sorter;
	
	public RenderWrapper(Sorter sorter) {
		this.sorter = sorter;
	}

	@Override
	public Options getOptions() {
		Options options =  super.getOptions().addOption(Option.builder(OPT_NAME_SHORT).longOpt(OPT_NAME).desc("Sets the name for the sorting").hasArg().build());
		options.addOption(Option.builder(OPT_FORCE_SHORT).longOpt(OPT_FORCE).desc("Forces overwriting ").build());
		options.addOption(Option.builder(OPT_GROUP_SHORT).longOpt(OPT_GROUP).desc("Sets the group to be rendered").hasArg().build());
		options.addOption(Option.builder(OPT_RENDERFILTER_SHORT).longOpt(OPT_RENDERFILTER).desc("Sets the renderfilter string.\nThe format is as follows:\nfilter1,param1,param2:filter2,param1\nPossible filters are:\n\ttype,filename_suffix_to_allow\n  pp3,key,value_to_allow\n  pp3max,key,max_value\n  pp3min,key,min_value\n  pp3minmax,key,min_value,max_value\n  pp3rate,allowed_ratings_multiple_possible\n  pp3color,colors").hasArg().build());
		options.addOption(Option.builder(OPT_PREVENT_CLEANUP_SHORT).longOpt(OPT_PREVENT_CLEANUP).desc("Do not cleanup the rendered files").build());
		return options;
	}

	@Override
	public Operation getOperation(CommandLine cmdLine, Path workingDir) {
		Render render = new Render(sorter);
		if (cmdLine.hasOption(OPT_NAME)) {
			render.setSortingName(cmdLine.getOptionValue(OPT_NAME));
		} else {
			render.setSortingName(SortingHelper.detectSortingFromDir(workingDir));
		}
		if (cmdLine.hasOption(OPT_GROUP)) {
			render.setNameOfGroup(cmdLine.getOptionValue(OPT_GROUP));
		}
		if (cmdLine.hasOption(OPT_FORCE)) {
			render.setForce(true);
		}
		if (cmdLine.hasOption(OPT_PREVENT_CLEANUP)) {
			render.setPreventCleanup(true);
		}
		if (cmdLine.hasOption(OPT_RENDERFILTER)) {
			String renderFilterString = cmdLine.getOptionValue(OPT_RENDERFILTER);
			
			Collection<RenderFilter> renderFilters = new HashSet<>();
			
			for (String currentFilterString : renderFilterString.split(":")) {
				String[] filterArray = currentFilterString.split(",");
				switch (filterArray[0]) {
					case "type":
						renderFilters.add(new FileTypeFilter(filterArray[1]));
						break;
					case "pp3":
						renderFilters.add(new Pp3RenderFilter(filterArray[1], filterArray[2]));
						break;
					case "pp3max":
						renderFilters.add(new Pp3MaxIntRenderFilter(filterArray[1], Integer.parseInt(filterArray[2])));
						break;
					case "pp3min":
						renderFilters.add(new Pp3MinIntRenderFilter(filterArray[1], Integer.parseInt(filterArray[2])));
						break;
					case "pp3minmax":
						renderFilters.add(new Pp3MinMaxIntRenderFilter(filterArray[1], Integer.parseInt(filterArray[2]), Integer.parseInt(filterArray[3])));
						break;
					case "pp3rate":
						int [] ratings = new int [filterArray[1].length()];
						for (int i = 0; i < filterArray[1].length(); i++) {
							ratings[i] = Integer.parseInt(filterArray[1].substring(i, i+1));
						}
						renderFilters.add(new Pp3IntFilter("Rank","R", ratings));
						break;
					case "pp3color":
						Collection<Integer> colors = new HashSet<>();
						if (filterArray[1].contains("red")) {
							colors.add(1);
						}
						if (filterArray[1].contains("yellow")) {
							colors.add(2);
						}
						if (filterArray[1].contains("green")) {
							colors.add(3);
						}
						if (filterArray[1].contains("blue")) {
							colors.add(4);
						}
						if (filterArray[1].contains("purple")) {
							colors.add(5);
						}
						renderFilters.add(new Pp3IntFilter("ColorLabel","C", colors.stream().mapToInt(i -> i).toArray()) {
							@Override
							public String getShortString() {
								return super.getShortString()
										.replace("0", "n")
										.replace("1", "r")
										.replace("2", "y")
										.replace("3", "g")
										.replace("4", "b")
										.replace("5", "p");
							}
						});
						break;
					default:
						throw new RuntimeException("Unknown filter " + filterArray[0]);
				}
			}
			
			render.setRenderFilters(renderFilters);
		}
		return render;
	}

	@Override
	public String getHelp() {
		return "Renders a sorting to create the result files";
	}

}
