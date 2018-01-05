package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;

public class RenderScriptMultiRenderJob implements RenderJob {

	private Collection<Group> groups;
	private Path renderscriptFile;

	public RenderScriptMultiRenderJob(Path renderscriptFile, Collection<Group> groups) {
		this.groups = groups;
		this.renderscriptFile = renderscriptFile;
	}

	@Override
	public void store(Path destination) throws IOException {
		Path workDir = Files.createTempDirectory("camtool");
		for (Group group : groups) {
			group.getRenderJob().store(workDir);
		}

		List<Path> arguments = new LinkedList<>();
		try {
			Files.list(workDir).forEach(file -> {arguments.add(file.toAbsolutePath());});
		} catch (IOException e) {
			throw new IllegalStateException("Multi group rendering failed", e);
		}
		
		if (!execRenderScript(renderscriptFile, arguments)) {
			throw new IllegalStateException("Render script execution failed");
		}
	}

	public static boolean execRenderScript(Path script, List<Path> arguments){
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(script))){
			engine.put("_files", arguments);
			engine.eval(reader);
		} catch (ScriptException | IOException e) {
			Logger.log("Failed to run script correctly", e, Level.ERROR);
			return false;
		}
		return true;
	}
	
}
