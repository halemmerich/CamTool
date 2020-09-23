package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;
import de.dieklaut.camtool.util.FileUtils;

public class ModifyTimestamp extends AbstractOperation {

	private long difference = 0;
	private String selectingRegex;
	private String timestamp;

	public ModifyTimestamp() {
	}
	
	public void setRegex(String regex) {
		this.selectingRegex = regex;
	}
	
	public void setDifference(long ms) {
		this.difference = ms;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public void perform(Context context) {
		if (selectingRegex == null || selectingRegex.isEmpty()) {
			throw new IllegalArgumentException("Regex for file selection is needed");
		}
		Collection<Path> filesToBeShifted;
		try {
			filesToBeShifted = FileUtils.getByRegex(context.getTimeLine(), selectingRegex);
			for (Path file : filesToBeShifted) {
				if (difference != 0) {
					shiftFile(file, context, difference);	
				} else if (timestamp != null && !timestamp.isEmpty()) {
					shiftFile(file, context, timestamp);
				} else {
					Logger.log("Error during shifting, no time change data found", Level.WARNING);
				}
			}
		} catch (IOException e) {
			Logger.log("Error during shifting", e);
		}
	}

	private void shiftFile(Path file, Context context, String timestamp) throws IOException {
		String name = FileUtils.getNamePortion(file);
		FileUtils.renameFile(file, context.getRoot().resolve(Constants.FOLDER_SORTED), FileUtils.buildFileName(timestamp, name + FileUtils.getSuffix(file)));
	}

	private void shiftFile(Path file, Context context, long difference) throws IOException {
		long timestamp = FileUtils.getTimestampPortionEpoch(file);
		String name = FileUtils.getNamePortion(file);
		FileUtils.renameFile(file, context.getRoot().resolve(Constants.FOLDER_SORTED), FileUtils.buildFileName(timestamp + difference, name + FileUtils.getSuffix(file)));
	}
}
