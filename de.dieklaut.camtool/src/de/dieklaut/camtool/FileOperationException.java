package de.dieklaut.camtool;

import java.io.IOException;

public class FileOperationException extends Exception {
	
	private static final long serialVersionUID = -602099385776510198L;

	public FileOperationException(String message, IOException e) {
		super(message, e);
	}

	public FileOperationException(String message) {
		super(message);
	}

}
