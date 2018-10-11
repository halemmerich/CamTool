package de.dieklaut.camtool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import de.dieklaut.camtool.util.FileUtils;

public class TestFileHelper {
	
	public static Path getTestResource(String path) {
		return Paths.get(System.getenv("RESOURCES_PATH")).resolve(Paths.get(path));
	}
	
	public static Path createFileWithModifiedDate(Path path, long timestamp) throws IOException {
		return Files.setLastModifiedTime(Files.createFile(path), FileTime.fromMillis(timestamp));
	}
	
	public static Context createComplexContext(Path folder) throws IOException {
		Context context = Context.create(folder);
		Files.createDirectory(folder.resolve(Constants.FOLDER_TIMELINE));
		Path sorted = Files.createDirectory(folder.resolve(Constants.FOLDER_SORTED));
		Path sorting = Files.createDirectory(sorted.resolve(Constants.DEFAULT_SORTING_NAME));
		Files.createFile(sorting.resolve(Constants.SORTED_FILE_NAME));
		Files.createDirectory(folder.resolve(Constants.FOLDER_ORIGINAL));
		
		return context;
	}

	public static Path addFileToSorting(Context context, Path file, long timestamp) throws IOException {
		Path origFile = createFileWithModifiedDate(context.getOriginals().resolve(file.getFileName()), timestamp);
		Path timelineLink = Files.createSymbolicLink(context.getTimeLine().resolve(FileUtils.buildFileName(FileUtils.getTimestamp(origFile), file.getFileName().toString())), context.getTimeLine().relativize(origFile));
		Path sorting = context.getRoot().resolve(Constants.FOLDER_SORTED).resolve(Constants.DEFAULT_SORTING_NAME);
		if (file.getParent() != null) {
			sorting = sorting.resolve(file.getParent());
			Files.createDirectories(sorting);
		}
		
		return Files.createSymbolicLink(sorting.resolve(timelineLink.getFileName()), sorting.relativize(timelineLink));
	}
}
