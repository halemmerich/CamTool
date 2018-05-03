package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.scriptapi.FilesApi;

public class JavaScriptExecutor {

	/**
	 * This executes a given script file in a java script runner and provides some
	 * environment.
	 * 
	 * @param script
	 *            a script file to be executed
	 * @param resultDir
	 *            the folder where the end results will be stored
	 * @param workingDir
	 *            a folder for work, it is expected to be cleanup up by the caller
	 * @param globalVariables
	 *            Additional variables to provide in the environment
	 * @return
	 */
	public static boolean execRenderScript(Path script, Path resultDir, Path workingDir,
			Map<String, Object> globalVariables) {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(script))) {
			engine.put("_resultDir", resultDir.toAbsolutePath().toString());
			engine.put("_workingDir", workingDir.toAbsolutePath().toString());
			engine.eval("FilesApi = Java.type(\"" + FilesApi.class.getName() + "\")");
			engine.eval("Paths = Java.type(\"" + Paths.class.getName() + "\")");
			for (String name : globalVariables.keySet()) {
				engine.put("_" + name, globalVariables.get(name));
			}
			
			engine.eval(reader);
		} catch (ScriptException | IOException e) {
			Logger.log("Failed to run script correctly", e, Level.ERROR);
			return false;
		}
		return true;
	}

}
