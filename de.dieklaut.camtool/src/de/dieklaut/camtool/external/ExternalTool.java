package de.dieklaut.camtool.external;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.StringJoiner;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;

public abstract class ExternalTool {
	
	public abstract CommandLine getCommandLine();
	
	private String output;

	/**
	 * Executes the command line build by {@link #getCommandLine()}
	 * @param workingDir 
	 * @param keepStdOut
	 * 
	 * @return true, iff successfully processed
	 */
	public boolean process(Path workingDir, boolean keepStdOut) {
		Executor executor = new DefaultExecutor();
		try (ByteArrayOutputStream stdout = new ByteArrayOutputStream()) {
		    PumpStreamHandler psh = new PumpStreamHandler(stdout);
		    executor.setStreamHandler(psh);
		    if (workingDir != null) {
		    	executor.setWorkingDirectory(workingDir.toAbsolutePath().normalize().toFile());
		    }
			
		    try {
		    	CommandLine commandLine = getCommandLine();
				Logger.log("Process command line: " + getAsString(commandLine), Level.TRACE);
				executor.execute(commandLine);
				if (keepStdOut){
					this.output = stdout.toString();
				}
		    } catch (ExecuteException e) {
		    	int exitValue = e.getExitValue();
				Logger.log("Process return value: " + exitValue, Level.DEBUG);
				Logger.log("Process output:\n" + stdout.toString(), Level.TRACE);
				Logger.log("External tool failed with return code " + exitValue, Level.ERROR);
				return false;
		    }
		} catch (IOException e) {
			Logger.log("External tool failed", e);
			return false;
		}
		return true;
	}

	/**
	 * Executes the command line build by {@link #getCommandLine()}
	 * @param workingDir 
	 * 
	 * @return true, iff successfully processed
	 */
	public boolean process(Path workingDir) {
		return process(workingDir, false);
	}
	
	public boolean process(boolean keepStdOut) {
		return process(null, keepStdOut);
	}
	
	public boolean process() {
		return process(null);
	}

	public String getOutput() {
		return output;
	}

	private String getAsString(CommandLine commandLine) {
		StringJoiner joiner = new StringJoiner(" ");
		for (String current : commandLine.toStrings()) {
			if (current.contains(" ")) {
				joiner.add('"' + current + '"');
			} else {
				joiner.add(current);
			}
		}
		return joiner.toString();
	}
}
