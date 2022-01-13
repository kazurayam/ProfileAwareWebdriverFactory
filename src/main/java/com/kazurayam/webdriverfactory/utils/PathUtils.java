package com.kazurayam.webdriverfactory.utils;

import groovy.lang.Closure;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class PathUtils {
    public static List<Path> listDirectoryRecursively(Path dir) throws IOException {
        return Files.walk(dir).collect(Collectors.toList());
    }

    public static int copyDirectoryRecursively(final Path sourceDir, final Path destinationDir)
            throws IOException
    {
        List<Path> sources = Files.walk(sourceDir).collect(Collectors.toList());
        List<Path> destinations = sources.stream()
                .map(sourceDir::relativize)
                .map(destinationDir::resolve)
                .collect(Collectors.toList());
        for (int i = 0; i < sources.size() ; i++){
            Files.copy(sources.get(i), destinations.get(i), StandardCopyOption.REPLACE_EXISTING);
        }

        return sources.size();
    }

    public static void deleteDirectoryRecursively(Path rootPath) {
        ResourceGroovyMethods.deleteDir(rootPath.toFile());
    }

}
