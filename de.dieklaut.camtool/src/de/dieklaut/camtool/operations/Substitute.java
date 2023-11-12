package de.dieklaut.camtool.operations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;

public class Substitute extends AbstractOperation{

	private String sortingName = Constants.DEFAULT_SORTING_NAME;
	private String nameOfGroup;
	private boolean switchExternal = false;
	private String [] substitutions;
	
	public Substitute() {
		super();
	}
	
	public void setSortingName(String sortingName) {
		this.sortingName = sortingName;
	}

	public void setNameOfGroup(String nameOfGroup) {
		this.nameOfGroup = nameOfGroup;
	}

	public void setSwitch(boolean switchExternal) {
		this.switchExternal = switchExternal;
	}

	public void setSubstitutions(String [] substitutions) {
		this.substitutions = substitutions;
	}

	@Override
	public void perform(Context context) {
		Path sortingFolder = context.getRoot().resolve(Constants.FOLDER_SORTED).resolve(sortingName);
		Path groupFolder = sortingFolder.resolve(nameOfGroup);
		Path renderSub = groupFolder.resolve(Constants.FILE_NAME_RENDERSUBSTITUTE);
		Path renderSubExt = groupFolder.resolve(Constants.FILE_NAME_RENDERSUBSTITUTE_EXTERNAL);
		try {
			if (switchExternal) {
				if (Files.exists(renderSub) && !Files.exists(renderSubExt)) {
					Files.move(renderSub, renderSubExt);
					Files.createFile(renderSub);
					List<String> externalFiles = Files.readAllLines(renderSubExt);
					for (String c : externalFiles) {
						if (Files.exists(groupFolder.resolve(c))) {
							Files.createSymbolicLink(sortingFolder.resolve(c), sortingFolder.relativize(groupFolder).resolve(c));
						}
					}
				} else if (Files.exists(renderSub)){
					if (Files.exists(renderSub)) Files.delete(renderSub);
					Files.move(renderSubExt, renderSub);
					List<String> externalFiles = Files.readAllLines(renderSub);
					for (String c : externalFiles) {
						Path currentPath = sortingFolder.resolve(c);
						if (Files.exists(currentPath) && Files.isSymbolicLink(currentPath)) {
							Files.delete(currentPath);
						}
					}
				} else {
					Logger.log("Switching not possible, neither " + Constants.FILE_NAME_RENDERSUBSTITUTE + " nor " + Constants.FILE_NAME_RENDERSUBSTITUTE_EXTERNAL + " found.", Level.WARNING);
				}
			} else {
				if (Files.exists(renderSub)) Files.delete(renderSub);
				StringJoiner joiner = new StringJoiner("\n");
				for (String c : substitutions) {
					if (!Files.exists(groupFolder.resolve(c))) {
						throw new FileNotFoundException("File " + c + " does not exist.");
					}
					joiner.add(c);
				}
				Files.write(renderSub, joiner.toString().getBytes());
			}
		} catch (IOException e) {
			Logger.log("Substitution failed" ,e);
		}
	}

}
