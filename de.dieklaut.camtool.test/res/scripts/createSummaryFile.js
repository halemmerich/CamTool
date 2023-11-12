var files = FilesApi.getFiles(_workingDir.toString());

for (var i = 0; i < files.length; i++){
	FilesApi.write(Paths.get(_resultDir).resolve("summary.txt").toString(), Paths.get(files[i]).getFileName().toString(), true);
}

