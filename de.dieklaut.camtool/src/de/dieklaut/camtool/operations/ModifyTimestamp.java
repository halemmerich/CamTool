package de.dieklaut.camtool.operations;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.util.FileUtils;

public class ModifyTimestamp extends AbstractOperation {

	private long difference;
	private String selectingRegex;

	public ModifyTimestamp() {
	}
	
	public void setRegex(String regex) {
		this.selectingRegex = regex;
	}
	
	public void setDifference(long ms) {
		this.difference = ms;
	}

	@Override
	public void perform(Context context) {
		Collection<Path> filesToBeShifted;
		try {
			filesToBeShifted = FileUtils.getByRegex(context.getTimeLine(), selectingRegex);
			for (Path file : filesToBeShifted) {
				shiftFile(file, context, difference);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void shiftFile(Path file, Context context, long difference) throws IOException {
		long timestamp = FileUtils.getTimestampPortionEpoch(file);
		String name = FileUtils.getNamePortion(file);
		FileUtils.renameFile(file, context.getRoot().resolve(Constants.FOLDER_SORTED), FileUtils.buildFileName(timestamp + difference, name + FileUtils.getSuffix(file)));
	}
}
