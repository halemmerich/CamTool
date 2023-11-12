package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.scriptapi.Combiner;
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
	public static boolean execRenderScript(Path script, String groupName, Path resultDir, Path workingDir,
			Map<String, Object> globalVariables) {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("Graal.js");
		ScriptContext context = engine.getContext();
		Bindings binding = new SimpleBindings();
		binding.put("polyglot.js.allowHostAccess", true);
		binding.put("polyglot.js.allowNativeAccess", true);
		binding.put("polyglot.js.allowCreateThread", true);
		binding.put("polyglot.js.allowIO", true);
		binding.put("polyglot.js.allowHostClassLookup", true);
		binding.put("polyglot.js.allowHostClassLoading", true);
		binding.put("polyglot.js.allowAllAccess", true);
		context.setBindings(binding, ScriptContext.GLOBAL_SCOPE);
		try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(script))) {
			
			binding.put("_groupName", groupName);
			binding.put("_resultDir", resultDir.toAbsolutePath().toString());
			binding.put("_workingDir", workingDir.toAbsolutePath().toString());
			eval(engine, "var FilesApi = Java.type('" + FilesApi.class.getName() + "');", binding);
			eval(engine, "var Paths = Java.type('" + Paths.class.getName() + "');", binding);
			eval(engine, "var Combiner = Java.type('" + Combiner.class.getName() + "\');", binding);
			for (String name : globalVariables.keySet()) {
				binding.put("_" + name, globalVariables.get(name));
			}
			
			engine.eval(reader, binding);
		} catch (ScriptException | IOException e) {
			Logger.log("Failed to run script correctly", e, Level.ERROR);
			return false;
		}
		return true;
	}

	private static void eval(ScriptEngine engine, String script, Bindings binding) throws ScriptException {
		Logger.log("Script eval:\n" + script, Level.TRACE);
		engine.eval(script, binding);
	}
}
