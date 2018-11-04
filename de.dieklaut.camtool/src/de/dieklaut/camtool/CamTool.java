package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.cmdlinewrapper.AbstractWrapper;
import de.dieklaut.camtool.cmdlinewrapper.CleanTrashWrapper;
import de.dieklaut.camtool.cmdlinewrapper.DeleteUnusedWrapper;
import de.dieklaut.camtool.cmdlinewrapper.ExplodeWrapper;
import de.dieklaut.camtool.cmdlinewrapper.ExportWrapper;
import de.dieklaut.camtool.cmdlinewrapper.InitWrapper;
import de.dieklaut.camtool.cmdlinewrapper.MoveWrapper;
import de.dieklaut.camtool.cmdlinewrapper.OperationWrapper;
import de.dieklaut.camtool.cmdlinewrapper.RenderWrapper;
import de.dieklaut.camtool.cmdlinewrapper.ShowGroupsWrapper;
import de.dieklaut.camtool.cmdlinewrapper.SimplifyWrapper;
import de.dieklaut.camtool.cmdlinewrapper.SortWrapper;
import de.dieklaut.camtool.cmdlinewrapper.UpdateUnusedWrapper;
import de.dieklaut.camtool.operations.AbstractOperation;
import de.dieklaut.camtool.operations.Operation;

public class CamTool {

	private static final String APPLICATION_NAME = "camtool";
	private static final String APPLICATION_ACTION = "<action>";
	private static final String APPLICATION_OPTIONS = "<options>";

	private static HelpFormatter formatter = new HelpFormatter();

	private static Option helpOption = new Option("h", "help", false, "Print this help");

	private static Options options = new Options().addOption(helpOption);

	private static Sorter sorter = new DefaultSorter();
	
	public static Path workingDir = Paths.get("").toAbsolutePath();

	private static UserInterface ui = new UserInterface() {

		@Override
		public void show(String text) {
			System.out.println(text);
		}

	};

	private static Engine engine = new Engine(new InitWrapper(), new SortWrapper(sorter), new CleanTrashWrapper(sorter),
			new RenderWrapper(sorter), new ShowGroupsWrapper(sorter, ui), new MoveWrapper(sorter), new SimplifyWrapper(sorter), new ExportWrapper(), new ExplodeWrapper(sorter),
			new UpdateUnusedWrapper(), new DeleteUnusedWrapper());

	private CamTool() {
		// Prevent instantiation
	}

	public static void main(String[] args) {
		Logger.setDiscardIfBelow(Level.TRACE);

		if (args.length == 0) {
			printGenericHelp();
		} else if (args[0].startsWith("-")) {
			System.out.println("An operation is needed.\n");
			printGenericHelp();
		} else {

			// extract action and move args

			OperationWrapper operationWrapper = null;
			for (OperationWrapper op : engine.getOperationWrappers()) {
				if (op.getName().toLowerCase().equals(args[0].toLowerCase())) {
					operationWrapper = op;
					break;
				}
			}

			if (operationWrapper == null) {
				Logger.log("No operation found on command line", Logger.Level.ERROR);
				operationWrapper = new AbstractWrapper() {

					@Override
					public Operation getOperation(CommandLine cmdLine, Path workingDir) {
						return new AbstractOperation() {

							@Override
							public void perform(Context context) {
								// Do nothing
							}
						};
					}

					@Override
					public String getHelp() {
						return "Invalid operation";
					}

					@Override
					public String getUsage() {
						return "";
					}

				};
			}

			CommandLineParser parser = new DefaultParser();

			try {
				Options operationOptions = operationWrapper.getOptions();
				CommandLine cmd = parser.parse(operationOptions, Arrays.copyOfRange(args, 1, args.length));
				if (cmd.hasOption(helpOption.getOpt())) {
					printOperationHelp(operationWrapper);
					return;
				}
				Context context = null;
				if (operationWrapper instanceof InitWrapper) {
					try {
						context = Context.create(workingDir);
					} catch (IOException e) {
						Logger.log("Context could not be created", e);
					}
				} else {
					context = findContext(workingDir);
				}

				Operation operation = operationWrapper.getOperation(cmd, workingDir);
				Logger.log("Performing operation " + operation.getName(), Level.INFO);
				operation.perform(context);
			} catch (ParseException e) {
				showHelp();
			} catch (NoContextFoundException e) {
				Logger.log("Could not find a context", e);
				showHelp();
			}
		}
	}

	private static void showHelp() {
		formatter.printHelp(APPLICATION_NAME + " " + APPLICATION_ACTION + " " + APPLICATION_OPTIONS, options);
	}

	private static Context findContext(Path workingDir) throws NoContextFoundException {
		if (Context.isInitialized(workingDir)) {
			return new Context(workingDir);
		}

		while (workingDir.getParent() != null) {
			workingDir = workingDir.getParent();
			if (Context.isInitialized(workingDir)) {
				return new Context(workingDir);
			}
		}

		throw new NoContextFoundException("Could not find a context at " + workingDir + " or at any parent");
	}

	private static void printGenericHelp() {
		System.out.println("Available operations: ");
		int length = 0;
		for (OperationWrapper wrapper : engine.getOperationWrappers()) {
			if (wrapper.getName().length() > length) {
				length = wrapper.getName().length();
			}
		}
		for (OperationWrapper wrapper : engine.getOperationWrappers()) {
			System.out.println(String.format(" %-" + (length + 3) + "s", wrapper.getName()) + wrapper.getHelp());
		}
	}

	private static void printOperationHelp(OperationWrapper wrapper) {
		formatter.printHelp(APPLICATION_NAME + " " + wrapper.getUsage(), wrapper.getOptions());
	}
}
