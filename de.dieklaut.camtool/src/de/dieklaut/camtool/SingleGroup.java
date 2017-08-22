package de.dieklaut.camtool;

import java.nio.file.Path;
import java.util.Collection;

import de.dieklaut.camtool.renderjob.CopyJob;
import de.dieklaut.camtool.renderjob.NullRenderJob;
import de.dieklaut.camtool.renderjob.RenderJob;
import de.dieklaut.camtool.renderjob.RenderJobFactory;

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

	public SingleGroup(Collection<Path> elements) {
		super(elements);
	}

	@Override
	public RenderJob getRenderJob() {
		if (isMarkedAsDeleted()) {
			return new NullRenderJob();
		}
		Collection<Path> elements = getAllFiles();
		
		Path toBeRendered = null;
		
		for (Path element : elements) {
			if (FileTypeHelper.isRawImageFile(element) || FileTypeHelper.isVideoFile(element)) {
				toBeRendered = element;
				break;
			}
		}
		
		if (toBeRendered != null) {
			elements.remove(toBeRendered);
			return RenderJobFactory.getInstance().forFile(toBeRendered, elements.toArray(new Path [elements.size()]));
		}
		
		return new CopyJob(elements.iterator().next());
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
}
