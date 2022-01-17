package com.kazurayam.webdriverfactory.utils;

import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactoryImpl;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

public class PathUtils {

    private static Logger logger_ = LoggerFactory.getLogger(PathUtils.class);

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
            try {
                Files.copy(sources.get(i), destinations.get(i), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                logger_.warn(e.getMessage());
            }
        }

        return sources.size();
    }

    public static void deleteDirectoryRecursively(Path rootPath) {
        ResourceGroovyMethods.deleteDir(rootPath.toFile());
    }

}
