package de.dieklaut.camtool;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;

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
	@Override
	public Collection<Path> getAllFiles() {
		Collection<Path> result = new HashSet<>();
		result.addAll(elements);
		return result;
	}

	@Override
	public RenderJob getRenderJob() {
		if (isMarkedAsDeleted()) {
			return new NullRenderJob();
		}
		Collection<Path> elements = getAllFiles();
		
		Path toBeRendered = getPrimaryFile();
		
		if (toBeRendered != null) {
			elements.remove(toBeRendered);
			return RenderJobFactory.getInstance().forFile(toBeRendered, elements.toArray(new Path [elements.size()]));
		}
		
		return new CopyRenderJob(elements.iterator().next());
	}
	
	private Path getPrimaryFile() {
		Path toBeRendered = null;
		for (Path element : getAllFiles()) {
			if (FileTypeHelper.isRawImageFile(element) || FileTypeHelper.isVideoFile(element)) {
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
		return "SingleGroup: " + getAllFiles();
	}

	@Override
	public String getType() {
		return "single";
	}
	
	public Instant getTimestamp () {
		try {
			if (cachedTimestamp == null) {
				cachedTimestamp = FileUtils.getCreationDate(getPrimaryFile());
			}
			return cachedTimestamp;
		} catch (FileOperationException e) {
			throw new IllegalStateException("Could not read creation date from primary file for group " + this, e);
		}
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
}
