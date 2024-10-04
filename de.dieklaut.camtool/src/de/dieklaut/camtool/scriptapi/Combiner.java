package de.dieklaut.camtool.scriptapi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.external.AlignImageStackWrapper;
import de.dieklaut.camtool.external.ConvertWrapper;
import de.dieklaut.camtool.external.EnfuseWrapper;

public class Combiner {
	
	public static void combine(Path inputDir, Path outputDir, String groupName, EnfuseWrapper enfuse) {
		try {
			AlignImageStackWrapper aligner = new AlignImageStackWrapper();
			try (var l = Files.list(inputDir)){
				aligner.setInputFile(l.toArray(size -> new Path [size]));
			}
			aligner.setPrefix("aligned");
			aligner.setOptimizeFieldOfViewFirstImage(true);
			
			if (aligner.process(inputDir)) {
				enfuse.setInputFiles(Files.list(inputDir).filter(path -> path.getFileName().toString().startsWith("aligned")).toArray(size -> new Path [size]));
			} else {
				enfuse.setInputFiles(Files.list(inputDir).toArray(size -> new Path [size]));
				Logger.log("Failure during alignment for " + groupName, Level.WARNING);
			}
			
			Path fused = outputDir.resolve("result.tif");
			enfuse.setOutputFile(fused);
			enfuse.process();
			
			ConvertWrapper convert = new ConvertWrapper();
			convert.setInputFile(fused);
			convert.setOutputFile(outputDir.resolve(groupName + ".jpg"));
			convert.process();
			Files.delete(fused);
		} catch (IOException e) {
			try {
				Files.write(outputDir.resolve(groupName + "_renderFailed.txt"), e.toString().getBytes());
			} catch (IOException e1) {
				Logger.log("Failure while writing error result file", e);
			}
			Logger.log("Failure while calculating merge of input files", e);
		}
	}
	
	public static void dri(Path inputDir, Path outputDir, String groupName) {
		EnfuseWrapper enfuse = new EnfuseWrapper();
		enfuse.setExposureOptimum(0.6);
		enfuse.setExposureWidth(0.3);
		enfuse.setSaturationWeight(0.5);
		combine(inputDir, outputDir, groupName, enfuse);
	}
	
	public static void nr(Path inputDir, Path outputDir, String groupName) {
		dri(inputDir, outputDir, groupName);
	}
	
	public static void pano(Path inputDir) {
		
	}
	
	public static void focus(Path inputDir, Path outputDir, String groupName) {
		EnfuseWrapper enfuse = new EnfuseWrapper();
		enfuse.setSaturationWeight(0);
		enfuse.setExposureWeight(0);
		enfuse.setContrastWeight(1);
		enfuse.setHardMask(true);
		combine(inputDir, outputDir, groupName, enfuse);
	}
}
