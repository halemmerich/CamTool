package de.dieklaut.camtool;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Context {

	private Path root;
	private Properties properties;

	public static boolean isInitialized(Path root) {
		return root.resolve(Constants.AUTOMATION_FILE_NAME).toFile().exists();
	}

	/**
	 * Creates a new {@link Context} and the needed files.
	 * 
	 * @param root
	 *            the {@link Path} to the context root
	 * @return the created {@link Context}
	 * @throws IOException
	 */
	public static Context create(Path root) throws IOException {
		if (!root.toFile().isDirectory()) {
			throw new IllegalArgumentException("Give root path " + root + " is not a directory");
		}

		if (isInitialized(root)) {
			throw new IllegalArgumentException("Give root path " + root + " already contains automation marker file");
		}

		Files.createFile(root.resolve(Constants.AUTOMATION_FILE_NAME));
		return new Context(root);
	}

	/**
	 * Creates a new {@link Context} object if the necessary files were created
	 * beforehand (using {@link Context#create(Path)}).
	 * 
	 * @param root
	 *            the {@link Path} to the context root
	 */
	public Context(Path root) {
		this.root = root;
		if (!root.toFile().isDirectory()) {
			throw new IllegalArgumentException("Give root path " + root + " is not a directory");
		}

		if (!isInitialized(root)) {
			throw new IllegalArgumentException("Give root path " + root + " does not contain automation marker file");
		}

		properties = new Properties();

		Path propertiesPath = root.resolve(Constants.FILE_PROPERTIES);
		if (propertiesPath.toFile().exists()) {
			try (InputStream inStream = Files.newInputStream(propertiesPath)) {
				properties.load(inStream);
			} catch (IOException e) {
				Logger.log("The properties file could not be read", e);
			}
		}
	}

	public int getFormatVersion() {
		return Integer.parseInt(
				properties.getProperty(Constants.PROPERTY_VERSION_FILE_FORMAT, Constants.CURRENT_VERSION_FILE_FORMAT));
	}

	public Path getRoot() {
		return root;
	}

	public void store() {
		try {
			properties.store(Files.newOutputStream(root.resolve(Constants.FILE_PROPERTIES)),
					"Properties for the CamTool");
		} catch (IOException e) {
			Logger.log("Storing the properties file failed", e);
		}
	}

	public Path getOriginals() {
		return root.resolve(Constants.FOLDER_ORIGINAL);
	}

	public Path getTimeLine() {
		return root.resolve(Constants.FOLDER_TIMELINE);
	}

	public String getName() {
		return root.getFileName().toString();
	}
}
