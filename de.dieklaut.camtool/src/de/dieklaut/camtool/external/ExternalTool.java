package de.dieklaut.camtool.external;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;

public abstract class ExternalTool {
	
	public abstract CommandLine getCommandLine();

	/**
	 * Executes the command line build by {@link #getCommandLine()}
	 * 
	 * @return true, iff successfully processed
	 */
	public boolean process() {
		Executor executor = new DefaultExecutor();
		try {
		    ByteArrayOutputStream stdout = new ByteArrayOutputStream();
		    PumpStreamHandler psh = new PumpStreamHandler(stdout);
		    executor.setStreamHandler(psh);			
			
		    try {
		    	CommandLine commandLine = getCommandLine();
				Logger.log("Process command line: " + commandLine.toString(), Level.TRACE);
				executor.execute(commandLine);
		    } catch (ExecuteException e) {
		    	int exitValue = e.getExitValue();
				Logger.log("Process return value: " + exitValue, Level.DEBUG);
				Logger.log("Process output:\n" + stdout.toString(), Level.TRACE);
				throw new IllegalStateException("External tool failed with return code " + exitValue);
		    }
		} catch (IOException e) {
			throw new IllegalStateException("External tool failed", e);
		}
		return true;
	}
}
