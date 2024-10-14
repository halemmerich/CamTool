package de.dieklaut.camtool.operations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.Logger;
import de.dieklaut.camtool.Logger.Level;

public class Substitute extends AbstractOperation{
	
	public enum Mode { INTERNAL, SWITCH, FIX, REMOVE };

	private String sortingName = null;
	private String nameOfGroup;
	private Mode mode = Mode.INTERNAL;
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

	public void setMode(Mode mode) {
		this.mode = mode;
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
			if (mode == Mode.SWITCH) {
				if (Files.exists(renderSub) && !Files.exists(renderSubExt)) {
					Files.move(renderSub, renderSubExt);
					Files.createFile(renderSub);
					List<String> externalFiles = Files.readAllLines(renderSubExt);
					for (String c : externalFiles) {
						if (Files.exists(groupFolder.resolve(c))) {
							Files.createSymbolicLink(groupFolder.getParent().resolve(c), groupFolder.getParent().relativize(groupFolder).resolve(c));
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
			} 
			if (mode == Mode.FIX) {
				// combine substitutions
				Collection<String> subs = new ArrayList<>();
				
				if (Files.exists(renderSub)) {
					List<String> files = Files.readAllLines(renderSub);
					for (String c : files) {
						Path current = groupFolder.resolve(c);
						if (Files.exists(current)) {
							subs.add(c);
						}
					}
				}
				
				if (Files.exists(renderSubExt)) {
					List<String> files = Files.readAllLines(renderSubExt);
					for (String c : files) {
						Path current = groupFolder.resolve(c);
						if (Files.exists(current)) {
							subs.add(c);
						}
					}
				}
				
				// write all subs into external file
				if (Files.exists(renderSubExt)) Files.delete(renderSubExt);
				StringJoiner joiner = new StringJoiner("\n");				
				for (String c : subs) {
					Path resolved = groupFolder.resolve(c);
					if (resolved.isAbsolute()) {
						resolved = groupFolder.relativize(resolved);
					}
					if (!Files.exists(groupFolder.resolve(resolved))) {
						throw new FileNotFoundException("File " + c + " does not exist.");
					}
					joiner.add(resolved.toString());
				}
				Files.write(renderSubExt, joiner.toString().getBytes());
				
				// create symlinks
				for (String c : subs) {
					Path currentPath = groupFolder.getParent().resolve(c);
					if (Files.exists(currentPath, LinkOption.NOFOLLOW_LINKS) && Files.isSymbolicLink(currentPath)) {
						Files.delete(currentPath);
					}
					if (!Files.exists(currentPath)) {
						Files.createSymbolicLink(groupFolder.getParent().resolve(c), groupFolder.getParent().relativize(groupFolder).resolve(c));
					}
				}
			} else if (mode == Mode.REMOVE){
				if (Files.exists(renderSub)) {
					Files.delete(renderSub);
				}
				if (Files.exists(renderSubExt)) {
					List<String> externalFiles = Files.readAllLines(renderSubExt);
					for (String c : externalFiles) {
						Path currentPath = sortingFolder.resolve(c);
						if (Files.exists(currentPath) && Files.isSymbolicLink(currentPath)) {
							Files.delete(currentPath);
						}
					}
					Files.delete(renderSubExt);
				}
			} else if (mode == Mode.INTERNAL){
				if (Files.exists(renderSub)) Files.delete(renderSub);
				StringJoiner joiner = new StringJoiner("\n");				
				for (String c : substitutions) {
					Path resolved = groupFolder.resolve(c);
					if (resolved.isAbsolute()) {
						resolved = groupFolder.relativize(resolved);
					}
					if (!Files.exists(groupFolder.resolve(resolved))) {
						throw new FileNotFoundException("File " + c + " does not exist.");
					}
					joiner.add(resolved.toString());
				}
				Files.write(renderSub, joiner.toString().getBytes());
			}
		} catch (IOException e) {
			Logger.log("Substitution failed" ,e);
		}
	}

}
