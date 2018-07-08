package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.dieklaut.camtool.util.FileUtils;

public abstract class AbstractGroup implements Group{

	@Override
	public boolean hasOwnFolder() {
		Path containing = getContainingFolder();
		try {
			return Files.list(containing).count() == getAllFiles().size();
		} catch (IOException e) {
			throw new IllegalStateException("File listing failed", e);
		}
	}

	@Override
	public Path getContainingFolder() {
		// This only works, because groups are expected to reside on one file system hierarchy level
		return getAllFiles().iterator().next().getParent();
	}

	@Override
	public void moveToFolder(Path destination) {
		getAllFiles().stream().forEach(current -> {
			if (Files.isSymbolicLink(current)) {
				try {
					FileUtils.moveSymlink(current, getTargetDestination(destination));
				} catch (IOException e) {
					throw new IllegalStateException("Moving a symlink failed", e);
				}
			} else {
				try {
					Files.move(current, getTargetDestination(destination));
				} catch (IOException e) {
					throw new IllegalStateException("Moving a file failed", e);
				}
			}
			FileUtils.cleanUpEmptyParents(current);
		});
	}

	private Path getTargetDestination(Path destination) {
		if (!destination.isAbsolute()) {
			return getContainingFolder().resolve(destination).toAbsolutePath();
		} else {
			return destination;
		}
	}

}
