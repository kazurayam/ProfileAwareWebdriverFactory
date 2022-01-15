package com.kazurayam.webdriverfactory.firefox;

import com.kazurayam.webdriverfactory.PreferencesModifier;
import com.kazurayam.webdriverfactory.PreferencesModifierBase;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public enum FirefoxPreferencesModifiers {
    ;

    public static PreferencesModifier downloadIntoUserHomeDownloadsDirectory()
            throws IOException
    {
        Path p = Paths.get(System.getProperty("user.home"), "Downloads");
        return downloadIntoDirectory(p);
    }

    public static PreferencesModifier downloadIntoDirectory(final Path directory)
            throws IOException
    {
        Objects.requireNonNull(directory);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        PreferencesModifier pm = new PreferencesModifierBase(PreferencesModifier.Type.FIREFOX_downloadIntoDirectory, new Closure<Map<String, Object>>(null, null) {
            public Map<String, Object> doCall(Map<String, Object> preferences) {
                preferences.put("browser.download.useDownloadDir", true);
                preferences.put("browser.download.folderList", 2);
                Path downloads = Paths.get(System.getProperty("user.home"), "Downloads");
                preferences.put("browser.download.dir", downloads.toString());
                return preferences;
            }

        });
        return pm;
    }

    public static PreferencesModifier downloadWithoutPrompt() {
        PreferencesModifier pm = new PreferencesModifierBase(PreferencesModifier.Type.FIREFOX_downloadWithoutPrompt, new Closure<Map<String, Object>>(null, null) {
            public Map<String, Object> doCall(Map<String, Object> preferences) {
                // set preference not to show file download confirmation dialog
                String mimeTypes = getAllMimeTypesAsString();
                //println "mimeTypes=${mimeTypes}"
                preferences.put("browser.helperApps.neverAsk.saveToDisk", mimeTypes);
                preferences.put("browser.helperApps.neverAsk.openFile", mimeTypes);
                return preferences;
            }

        });
        return pm;
    }

    private static final String getAllMimeTypesAsString() {
        return DefaultGroovyMethods.join(new ArrayList<String>(Arrays.asList("application/gzip", "application/java-archive", "application/json", "application/msexcel", "application/msword", "application/octet-stream", "application/pdf", "application/vnd-ms-office", "application/vnd-xls", "application/vnd.ms-excel", "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/x-dos_mx_excel", "application/x-excel", "application/x-ms-excel", "application/x-msexcel", "application/x-tar", "application/x-xls", "application/x-zip-compressed", "application/xls", "application/xml", "application/zip", "application/zlib", "image/bmp", "image/gif", "image/jpeg", "image/png", "image/svg+xml", "text/csv", "text/plain", "text/xml")), ",");
    }

}
