package de.dieklaut.camtool;

import java.time.Instant;

/**
 * A {@link SourceFile} is the initial artifact of a {@link Group}
 * @author mboonk
 *
 */
public interface SourceFile {
	public Instant getCreationDate() throws FileOperationException;
}
