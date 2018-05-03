package de.dieklaut.camtool.renderjob;

import java.io.IOException;
import java.nio.file.Path;

public class NullRenderJob extends RenderJob {

	@Override
	void storeImpl(Path destination) throws IOException {
		// do nothing
	}

}
