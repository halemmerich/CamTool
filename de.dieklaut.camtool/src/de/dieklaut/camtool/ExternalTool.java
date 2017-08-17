package de.dieklaut.camtool;

import java.io.IOException;

public abstract class ExternalTool {
	
	public abstract String getCommandLine();

	/**
	 * Executes the command line build by {@link #getCommandLine()}
	 * 
	 * @return true, iff successfully processed
	 */
	public boolean process() {
		ProcessBuilder pbuilder = new ProcessBuilder(getCommandLine());
		Process process;
		try {
			process = pbuilder.start();
			int returnValue = process.waitFor();
			
			if (returnValue != 0) {
				throw new IllegalStateException("External tool failed with return code " + returnValue);
			}
		} catch (InterruptedException | IOException e) {
			throw new IllegalStateException("External tool failed", e);
		}
		return true;
	}
}
