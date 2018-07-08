package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import de.dieklaut.camtool.renderjob.MultiRenderJob;
import de.dieklaut.camtool.renderjob.RenderJob;
import de.dieklaut.camtool.renderjob.RenderScriptMultiRenderJob;
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
	private Path collectionFile;
	private Path renderscriptFile;

	public MultiGroup(Collection<Group> groups, Path collectionFile) {
		this.groups = groups;
		this.collectionFile = collectionFile;
	}

	public MultiGroup(Collection<Group> groups) {
		this.groups = groups;
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
		return "MultiGroup: " + getName() + "\n" + getAllFiles();
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
	
	public Path getCollectionFile() {
		return collectionFile;
	}

	@Override
	public RenderJob getRenderJob() {
		if (renderscriptFile != null) {
			return new RenderScriptMultiRenderJob(renderscriptFile, this);	
		} else {
			return new MultiRenderJob(groups);
		}
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
		if (collectionFile != null) {
			return FileUtils.getNamePortion(collectionFile);
		} else {
			if (hasOwnFolder()) {
				return getContainingFolder().getFileName().toString();
			} else {Optional<Group> first = groups.stream().min(new GroupTimestampComparator());

				if (!first.isPresent()) {
					throw new IllegalStateException("No first element found while sorting groups, this should not happen");
				}
			
				return first.get().getName();
			}
		}
		
	}

	public void setRenderscriptFile(Path renderscriptFile) {
		this.renderscriptFile = renderscriptFile;
	}
	
	@Override
	public void moveToFolder(Path destination) {
		if (!destination.isAbsolute()) {
			try {
				destination = getContainingFolder().resolve(destination).toRealPath(LinkOption.NOFOLLOW_LINKS);
			} catch (IOException e) {
				throw new IllegalArgumentException("Could not resolve destination path " + destination + " for multi group move", e);
			}
		}
		try {
			if (Files.list(destination).filter(path -> !Files.isDirectory(path)).count() == 0) {
				// This means the render and collection file if any need to be converted
				if (renderscriptFile != null) {
					Files.move(renderscriptFile, destination.resolve(FileUtils.getGroupName(destination.getFileName())));
				}
				if (collectionFile != null) {
					Files.delete(collectionFile);
				}
			} else {
				if (collectionFile != null) {
					Files.move(collectionFile, destination.resolve(this.getName() + Constants.FILE_NAME_COLLECTION_SUFFIX));
				} else {
					collectionFile = destination.resolve(getName() + Constants.FILE_NAME_COLLECTION_SUFFIX);
					createCollectionFile();
				}
				if (renderscriptFile != null) {
					Files.move(renderscriptFile, destination.resolve(FileUtils.getGroupName(destination.getFileName())));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.moveToFolder(destination);
		
	}

	private void createCollectionFile() throws IOException {
		Files.createFile(collectionFile);
		for (Group g : groups) {
			Files.write(collectionFile, g.getName().getBytes(), StandardOpenOption.APPEND);
		}
	}
}
