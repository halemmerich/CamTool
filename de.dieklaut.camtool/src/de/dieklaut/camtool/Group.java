package de.dieklaut.camtool;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;

import de.dieklaut.camtool.operations.RenderFilter;
import de.dieklaut.camtool.renderjob.RenderJob;

/**
 * A group is an envelope for multiple files belonging together. They are expected to be on the same file hierarchy level.
 * 
 * @author mboonk
 *
 */
public interface Group{

	/**
	 * @return a collection of all files belonging to this group
	 */
	public Collection<Path> getAllFiles();

	/**
	 * Perform all necessary steps for creating the final result of this group
	 */
	public RenderJob getRenderJob(Collection<RenderFilter> renderFilters);
	
	/**
	 * @return true, iff this group has been marked as deleted
	 */
	public boolean isMarkedAsDeleted();
	
	/**
	 * @return true, iff only files of this group are contained in the highest parent folder
	 */
	public boolean hasOwnFolder();
	
	/**
	 * @return the path of the folder containing the group elements
	 */
	public Path getContainingFolder();
	
	/**
	 * @return a type description usable in file names
	 */
	public String getType();
	
	/**
	 * This moves all file belonging to this {@link Group} into another directory
	 * @param destination the target directory
	 */
	public void moveToFolder(Path destination);
	
	/**
	 * @return the creation timestamp for this group
	 */
	public Instant getTimestamp();
	
	/**
	 * @return the creation duration for this group
	 */
	public Duration getDuration();
	
	/**
	 * @return the name for this group
	 */
	public String getName();
	
	public String getCreator();
}
