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
import de.dieklaut.camtool.cmdlinewrapper.CleanTrashWrapper;
import de.dieklaut.camtool.cmdlinewrapper.DeleteUnusedWrapper;
import de.dieklaut.camtool.cmdlinewrapper.ExportWrapper;
import de.dieklaut.camtool.cmdlinewrapper.InitWrapper;
import de.dieklaut.camtool.cmdlinewrapper.OperationWrapper;
import de.dieklaut.camtool.cmdlinewrapper.RenderWrapper;
import de.dieklaut.camtool.cmdlinewrapper.SortWrapper;
import de.dieklaut.camtool.cmdlinewrapper.UpdateUnusedWrapper;
import de.dieklaut.camtool.operations.Operation;
import de.dieklaut.camtool.renderjob.DefaultRenderJobFactoryProvider;
import de.dieklaut.camtool.renderjob.RenderJobFactory;

public class CamTool {

	private static final String APPLICATION_NAME = "camtool";
	private static final String APPLICATION_ACTION = "<action>";
	private static final String APPLICATION_OPTIONS = "<options>";

	private static HelpFormatter formatter = new HelpFormatter();

	private static Option helpOption = new Option("h", "help", false, "Print this help");
	
	private static Options options = new Options().addOption(helpOption);
	
	private static Engine engine = new Engine(new InitWrapper(), new SortWrapper(), new CleanTrashWrapper(), new RenderWrapper(), new ExportWrapper(), new UpdateUnusedWrapper(), new DeleteUnusedWrapper());
	
	private CamTool() {
		//Prevent instantiation
	}
	
	public static void main(String [] args) {
		Logger.setDiscardIfBelow(Level.TRACE);
		
		if (args.length == 0) {
			printGenericHelp();
		} else if (args[0].startsWith("-")) {
			System.out.println("An operation is needed.\n");
			printGenericHelp();
		} else {
			
			//extract action and move args

			OperationWrapper operationWrapper = null;
			for (OperationWrapper op : engine.getOperationWrappers()) {
				if (op.getName().toLowerCase().equals(args[0].toLowerCase())){
					operationWrapper = op;
					break;
				}
			}
			
			if (operationWrapper == null) {
				Logger.log("No operation found on command line", Logger.Level.ERROR);
			}
			
			CommandLineParser parser = new DefaultParser();
			
			try {
				Options operationOptions = operationWrapper.getOptions();
				CommandLine cmd = parser.parse(operationOptions, Arrays.copyOfRange(args, 1, args.length));
				if (cmd.hasOption(helpOption.getOpt())){
					printOperationHelp(operationWrapper.getName(), operationOptions);
					return;
				}
				Context context = null;
				Path workingDir = Paths.get("").toAbsolutePath();
				if (operationWrapper instanceof InitWrapper) {
					try {
						context = Context.create(workingDir);
					} catch (IOException e) {
						Logger.log("Context could not be created", e);
					}
				} else {
					context = findContext(workingDir);
				}
				
				DefaultRenderJobFactoryProvider jobfactory = new DefaultRenderJobFactoryProvider();
				jobfactory.setUseRawtherapee(true);
				RenderJobFactory.setFactoryInstance(jobfactory);
				
				Operation operation = operationWrapper.getOperation(cmd);
				Logger.log("Performing operation " + operation.getName(), Level.INFO);
				operation.perform(context);
			} catch (ParseException e) {
				formatter.printHelp(APPLICATION_NAME + " " + APPLICATION_ACTION + " " + APPLICATION_OPTIONS, options);
			}
		}
	}

	private static Context findContext(Path workingDir) {
		if (Context.isInitialized(workingDir)) {
			return new Context(workingDir);
		}
		
		while (workingDir.getParent() != null) {
			workingDir = workingDir.getParent();
			if (Context.isInitialized(workingDir)) {
				return new Context(workingDir);
			}
		}
		
		throw new IllegalArgumentException("Could not find a context at " + workingDir + " or at any parent");
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

	private static void printOperationHelp(String operation, Options operationOptions) {
		formatter.printHelp(APPLICATION_NAME + " " + operation + " " + APPLICATION_OPTIONS, operationOptions);
	}
}
