var files = FilesApi.getFiles(_workingDir);

for (var i = 0; i < files.length; i++){
	FilesApi.write(Paths.get(_resultDir).resolve("summary.txt"), Paths.get(files[i]).getFileName(), true);
}

