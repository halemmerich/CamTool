package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
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
	private RenderModifier renderModifier;

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
		return "MultiGroup: " + getName() + "\n" + getGroups();
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
		if (renderModifier != null) {
			return renderModifier.getRenderJob();	
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
			Path containingFolder = getContainingFolder();
			if (Files.exists(containingFolder.resolve(Constants.SORTED_FILE_NAME))) {
				Optional<Group> first = groups.stream().min(new GroupTimestampComparator());

				if (!first.isPresent()) {
					throw new IllegalStateException("No first element found while sorting groups, this should not happen");
				}
			
				return first.get().getName();
			} else {
				return containingFolder.getFileName().toString();
			}
		}
		
	}
	
	@Override
	public boolean hasOwnFolder() {
		return collectionFile == null;
	}

	public void setRenderModifier(RenderModifier renderModifier) {
		this.renderModifier = renderModifier;
	}
	
	@Override
	public void moveToFolder(Path destination) {
		if (!destination.isAbsolute()) {
			destination = getContainingFolder().resolve(destination).toAbsolutePath().normalize();
		}
		
		if (!Files.exists(destination)) {
			try {
				Files.createDirectories(destination);
			} catch (IOException e) {
				Logger.log("Failure during creation of destination folder", e);
				throw new IllegalArgumentException(e);
			}
		}
		
		try {
			if (Files.list(destination).filter(path -> !Files.isDirectory(path)).count() == 0) {
				// This means the render and collection file if any need to be converted
				if (renderModifier != null) {
					renderModifier.move(destination.resolve(FileUtils.getGroupName(destination.getFileName())));
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
				if (renderModifier != null) {
					renderModifier.move(destination.resolve(FileUtils.getGroupName(destination.getFileName())));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Group g : getGroups()) {
			g.moveToFolder(destination.resolve(g.getName()));
		}
	}

	private void createCollectionFile() throws IOException {
		Files.createFile(collectionFile);
		for (Group g : groups) {
			Files.write(collectionFile, (g.getName() + "\n").getBytes(), StandardOpenOption.APPEND);
		}
	}
	
	@Override
	public Path getContainingFolder() {
		// This assumes the collection file being at the top of the multi group hierarchy
		if (getCollectionFile() != null) {
			return getCollectionFile().getParent();
		}
		
		Path shortest = null;
		for (Group g : getGroups()) {
			Path current = g.getContainingFolder();
			if (g.hasOwnFolder()) {
				current = current.getParent();
			}

			if (shortest == null || shortest.toAbsolutePath().normalize().startsWith(current.normalize().toAbsolutePath().getParent())) {
				shortest = current.normalize().toAbsolutePath();
			}
		}

		if (renderModifier != null && (shortest == null || shortest.toAbsolutePath().normalize().startsWith(renderModifier.getContainingFolder().toAbsolutePath().normalize()))) {
			shortest = renderModifier.getContainingFolder().normalize().toAbsolutePath();
		}
		
		return shortest;
	}
}
