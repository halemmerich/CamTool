package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;

import de.dieklaut.camtool.util.FileUtils;

public abstract class AbstractGroup implements Group{
	
	private Collection<Path> elements;

	public AbstractGroup(Collection<Path> elements) {
		this.elements = elements;
	}

	@Override
	public Collection<Path> getAllFiles() {
		Collection<Path> result = new HashSet<>();
		result.addAll(elements);
		return result;
	}

	@Override
	public boolean hasOwnFolder() {
		Path containing = getContainingFolder();
		try {
			return Files.list(containing).count() == elements.size();
		} catch (IOException e) {
			throw new IllegalStateException("File listing failed", e);
		}
	}

	@Override
	public Path getContainingFolder() {
		// This only works, because groups are expected to reside on one file system hierarchy level
		return elements.iterator().next().getParent();
	}

	@Override
	public void moveToFolder(Path destination) {
		elements.stream().forEach(current -> {
			if (Files.isSymbolicLink(current)) {
				try {
					FileUtils.moveSymlink(current, destination);
				} catch (IOException e) {
					throw new IllegalStateException("Moving a symlink failed", e);
				}
			} else {
				try {
					Files.move(current, destination);
				} catch (IOException e) {
					throw new IllegalStateException("Moving a file failed", e);
				}
			}
		});
	}

}
