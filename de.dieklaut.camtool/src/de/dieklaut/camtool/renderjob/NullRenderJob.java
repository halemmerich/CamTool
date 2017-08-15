package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Path;

public class NullRenderJob implements RenderJob {

	@Override
	public void store(Path destination) throws IOException {
		// do nothing
	}

}
