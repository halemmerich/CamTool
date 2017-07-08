package de.dieklaut.camtool;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Context {
	
	private Path root;
	
	public Context(Path root) {
		this.root = root;
		if (!root.toFile().isDirectory()) {
			throw new IllegalArgumentException("Give root path " + root + " is not a directory");
		}
	}
	
	public boolean isInitialized() {
		return Paths.get(root.toString(), ".automation").toFile().exists();
	}
	
	public Path getRoot() {
		return root;
	}
}
