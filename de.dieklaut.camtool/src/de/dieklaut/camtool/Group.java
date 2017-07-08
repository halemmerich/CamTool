package de.dieklaut.camtool;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;

public interface Group {
	public SourceFile getSourceFile();

	/**
	 * @return a collection of all files belonging to this group
	 */
	public Collection<File> getAllFiles();

	/**
	 * Perform all necessary steps for creating the final result of this group
	 */
	public Result render();
	
	/**
	 * @return true, iff this group has been marked as deleted
	 */
	public boolean isMarkedAsDeleted();
	
	/**
	 * This moves all file belonging to this {@link Group} into another directory
	 * @param destination the target directory
	 */
	public void moveToFolder(Path destination);
}
