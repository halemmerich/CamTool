package de.dieklaut.camtool;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import de.dieklaut.camtool.renderjob.RenderJob;
import de.dieklaut.camtool.util.FileUtils;

/**
 * This stores multiple files belonging together but essentially not the same.
 * This could be the source files for a panorama or a HDR-Stack. These files
 * must normally somehow processed to be useful.
 * 
 * @author mboonk
 *
 */
public class MultiGroup extends AbstractGroup {

	private Collection<Group> groups;
	private Path markerFile;

	public MultiGroup(Collection<Group> groups, Path markerFile) {
		this.groups = groups;
		this.markerFile = markerFile;
	}

	public MultiGroup(Collection<Group> groups) {
		this.groups = groups;
		Optional<Group> first = groups.stream().min(new GroupTimestampComparator());

		if (!first.isPresent()) {
			throw new IllegalStateException("No first element found while sorting groups, this should not happen");
		}
		
		this.markerFile = first.get().getContainingFolder().resolve(first.get().getName() + Constants.FILE_NAME_COLLECTION_SUFFIX);
	}

	@Override
	public boolean isMarkedAsDeleted() {
		for (Group group : groups) {
			if (!group.isMarkedAsDeleted()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "MultiGroup:\n" + getAllFiles();
	}

	@Override
	public String getType() {
		return "multi";
	}

	@Override
	public Collection<Path> getAllFiles() {
		Set<Path> paths = new HashSet<Path>();
		for (Group group : groups) {
			paths.addAll(group.getAllFiles());
		}
		return paths;
	}
	
	public Collection<Group> getGroups(){
		return new HashSet<>(groups);
	}
	
	public Path getMarkerFile() {
		return markerFile;
	}

	@Override
	public RenderJob getRenderJob() {
		return new MultiRenderJob(groups);
	}

	@Override
	public Instant getTimestamp() {
		Optional<Group> first = groups.stream().min(new GroupTimestampComparator());
		
		if (!first.isPresent()) {
			throw new IllegalStateException("No first element found while sorting groups, this should not happen");
		}
		
		return first.get().getTimestamp();
	}

	@Override
	public Duration getDuration() {
		Optional<Group> last = groups.stream().max(new GroupTimestampComparator());
		
		if (!last.isPresent()) {
			throw new IllegalStateException("No first element found while sorting groups, this should not happen");
		}
		
		return Duration.between(getTimestamp(), last.get().getTimestamp().plus(last.get().getDuration()));
	}

	@Override
	public String getName() {
		return FileUtils.getNamePortion(markerFile);
	}
}
