package com.kazurayam.webdriverfactory.utils

import java.nio.file.CopyOption
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.stream.Collectors

class PathUtils {

    static List<Path> listDirectoryRecursively(Path dir) {
        return Files.walk(dir).collect(Collectors.toList())
    }

    static int copyDirectoryRecursively(Path sourceDir, Path destinationDir) {
        List<Path> sources = Files.walk(sourceDir).collect(Collectors.toList())
        List<Path> destinations = sources.stream()
                .map({src -> sourceDir.relativize(src)})
                .map({dest -> destinationDir.resolve(dest)})
                .collect(Collectors.toList());
        for (int i = 0; i < sources.size(); i++) {
            Files.copy(sources.get(i), destinations.get(i), StandardCopyOption.REPLACE_EXISTING);
        }
        return sources.size()
    }

    static void deleteDirectoryRecursively(Path rootPath) {
        rootPath.toFile().deleteDir()
    }
}
