package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;

import de.dieklaut.camtool.operations.RenderFilter;
import de.dieklaut.camtool.renderjob.CopyRenderJob;
import de.dieklaut.camtool.renderjob.NullRenderJob;
import de.dieklaut.camtool.renderjob.RenderJob;
import de.dieklaut.camtool.renderjob.RenderJobFactory;
import de.dieklaut.camtool.util.FileUtils;

/**
 * This stores a single logical artifact, e.g. a single raw image file or
 * potentially multiple files that essentially belong together. This could be a
 * raw file with its accompanying out-of-camera JPG and rawtherapee picture
 * profile.
 * 
 * @author mboonk
 *
 */
public class SingleGroup extends AbstractGroup {

	private Collection<Path> elements;
	private Instant cachedTimestamp;
	private Duration cachedDuration;

	public SingleGroup(Collection<Path> elements) {
		this.elements = elements;
	}

	public SingleGroup(Path... elements) {
		this.elements = new HashSet<Path>();
		for (Path e : elements) {
			this.elements.add(e);
		}
	}

	@Override
	public Collection<Path> getAllFiles() {
		Collection<Path> result = new HashSet<>();
		result.addAll(elements);
		return result;
	}

	@Override
	public RenderJob getRenderJob(Collection<RenderFilter> renderFilters) {
		if (isMarkedAsDeleted() || isFiltered(renderFilters)) {
			return new NullRenderJob();
		}
		Path toBeRendered = getPrimaryFile();
		
		if (toBeRendered != null) {
			return forFile(toBeRendered, getHelperFiles().toArray(new Path[0]));
		}

		return new CopyRenderJob(toBeRendered);
	}
	
	private Collection<Path> getHelperFiles() {
		Collection<Path> allFiles = getAllFiles();
		allFiles.remove(getPrimaryFile());
		return allFiles;
	}
	private boolean isFiltered(Collection<RenderFilter> renderFilters) {
		if (renderFilters.size() == 0) {
			return false;
		}
		for (RenderFilter filter : renderFilters) {
			if (!filter.isFiltered(getPrimaryFile(), getHelperFiles())) {
				return false;
			}
		}
		return true;
	}

	private RenderJob forFile(Path mainFile, Path... helperFiles) {
		return RenderJobFactory.getRenderJob(mainFile, helperFiles, this.getName());
	}

	private Path getPrimaryFile() {
		Path toBeRendered = null;
		for (Path element : getAllFiles()) {
			if (FileTypeHelper.isRenderscript(element)) {
				toBeRendered = element;
				break;
			}
		}

		for (Path element : getAllFiles()) {
			if (toBeRendered == null && FileTypeHelper.isRawImageFile(element)) {
				toBeRendered = element;
				break;
			}
		}
		for (Path element : getAllFiles()) {
			if (toBeRendered == null && FileTypeHelper.isVideoFile(element)) {
				toBeRendered = element;
				break;
			}
		}
		for (Path element : getAllFiles()) {
			if (toBeRendered == null && FileTypeHelper.isImageFile(element)) {
				toBeRendered = element;
				break;
			}
		}

		if (toBeRendered == null) {
			return getAllFiles().iterator().next();
		}

		return toBeRendered;
	}

	@Override
	public boolean isMarkedAsDeleted() {
		for (Path element : getAllFiles()) {
			if (element.getFileName().endsWith(Constants.FILE_NAME_DELETED_SUFFIX)) {
				return true;
			}
			if (FileTypeHelper.isRawTherapeeProfile(element) && RawTherapeeParser.isDeleted(element)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "SingleGroup: " + getName() + "\n" + getAllFiles();
	}

	@Override
	public String getType() {
		return "single";
	}

	public Instant getTimestamp() {
		if (cachedTimestamp == null) {
			cachedTimestamp = FileUtils.getCreationDate(getPrimaryFile());
		}
		return cachedTimestamp;
	}

	@Override
	public Duration getDuration() {
		if (cachedDuration == null) {
			cachedDuration = FileUtils.getCreationDuration(getPrimaryFile());
		}
		return cachedDuration;
	}

	@Override
	public String getName() {
		return FileUtils.getGroupName(getPrimaryFile());
	}

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
		// This only works, because groups are expected to reside on one file system
		// hierarchy level
		return getAllFiles().iterator().next().toAbsolutePath().getParent();
	}

	@Override
	public void moveToFolder(Path destination) {
		getAllFiles().stream().forEach(current -> {
			Path targetDestination = getTargetDestination(destination);
			if (Files.isSymbolicLink(current)) {
				try {
					FileUtils.moveSymlink(current, targetDestination);
				} catch (IOException e) {
					throw new IllegalStateException("Moving a symlink failed", e);
				}
			} else {
				try {
					if (!Files.exists(targetDestination)) {
						Files.createDirectories(targetDestination);
					}
					Files.move(current, targetDestination.resolve(current.getFileName()));
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

	@Override
	public String getCreator() {
		return FileUtils.getCreator(getPrimaryFile());
	}
}
