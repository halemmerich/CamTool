package de.dieklaut.camtool;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Context implements AutoCloseable{
	
	private Path root;
	private Properties properties;
	
	public Context(Path root) {
		this.root = root;
		if (!root.toFile().isDirectory()) {
			throw new IllegalArgumentException("Give root path " + root + " is not a directory");
		}
		properties = new Properties();
		
		Path propertiesPath = root.resolve(Constants.FILE_PROPERTIES);
		if (propertiesPath.toFile().exists()) {
			InputStream inStream;
			try {
				inStream = Files.newInputStream(propertiesPath);
				properties.load(inStream);
			} catch (IOException e) {
				Logger.log("The properties file could not be read", e);
			}
		}
	}
	
	public boolean isInitialized() {
		return root.resolve(Constants.AUTOMATION_FILE_NAME).toFile().exists();
	}
	
	public int getFormatVersion() {
		return Integer.parseInt(properties.getProperty(Constants.PROPERTY_VERSION_FILE_FORMAT, Constants.CURRENT_VERSION_FILE_FORMAT));
	}
	
	public Path getRoot() {
		return root;
	}

	@Override
	public void close() {
		try {
			properties.store(Files.newOutputStream(root.resolve(Constants.FILE_PROPERTIES)), "Properties for the CamTool");
		} catch (IOException e) {
			Logger.log("Storing the properties file failed", e);
		}
	}
}
