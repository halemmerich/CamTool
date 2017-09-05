package de.dieklaut.camtool;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.exec.CommandLine;
import org.junit.Test;

import de.dieklaut.camtool.renderjob.RawTherapeeRenderJob;

public class RawTherapeeRenderJobTest{
	private int callCount;

	@Test
	public void testStore() throws IOException {
		Path rawFile = Paths.get("test.arw");
		Path destination = Paths.get("whatever");
		Path sidecar = Paths.get("test.pp3");
		
		RawTherapeeWrapper checkCommandlineCall = new RawTherapeeWrapper() {
			
			@Override
			public boolean process() {
				incrementCallCounter();
				return true;
			}

			@Override
			public CommandLine getCommandLine() {
				return null;
			}
		};
		
		RawTherapeeRenderJob job = new RawTherapeeRenderJob(checkCommandlineCall, rawFile, sidecar);
		job.store(destination);
		
		assertEquals(1, callCount);
	}

	protected synchronized void incrementCallCounter() {
		callCount ++;
	}
	
}
