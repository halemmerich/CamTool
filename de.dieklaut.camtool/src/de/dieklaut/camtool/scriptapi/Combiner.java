package de.dieklaut.camtool.scriptapi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.external.AlignImageStackWrapper;
import de.dieklaut.camtool.external.ConvertWrapper;
import de.dieklaut.camtool.external.EnfuseWrapper;

public class Combiner {
	public static void dri(Path inputDir, Path outputDir, String groupName) {
		try {
			AlignImageStackWrapper aligner = new AlignImageStackWrapper();
			aligner.setInputFile(Files.list(inputDir).toArray(size -> new Path [size]));
			aligner.setPrefix("aligned");
			
			EnfuseWrapper enfuse = new EnfuseWrapper();
			if (aligner.process(inputDir)) {
				enfuse.setInputFiles(Files.list(inputDir).filter(path -> path.getFileName().toString().startsWith("aligned")).toArray(size -> new Path [size]));
			} else {
				enfuse.setInputFiles(Files.list(inputDir).toArray(size -> new Path [size]));
			}
			
			Path fused = outputDir.resolve("result.tif");
			enfuse.setOutputFile(fused);
			enfuse.setExposureOptimum(0.6);
			enfuse.setExposureWidth(0.3);
			enfuse.setSaturationWeight(0.5);
			enfuse.process();
			
			ConvertWrapper convert = new ConvertWrapper();
			convert.setInputFile(fused);
			convert.setOutputFile(outputDir.resolve(groupName + ".jpg"));
			convert.process();
			Files.delete(fused);
		} catch (IOException e) {
			Logger.log("Failure while calculating merge of input files", e);
		}
	}
	
	public static void nr(Path inputDir, Path outputDir, String groupName) {
		dri(inputDir, outputDir, groupName);
	}
	
	public static void pano(Path inputDir) {
		
	}
}
