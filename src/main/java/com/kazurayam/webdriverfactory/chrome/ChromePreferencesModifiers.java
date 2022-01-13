package com.kazurayam.webdriverfactory.chrome;

import com.kazurayam.webdriverfactory.PreferencesModifier;
import com.kazurayam.webdriverfactory.PreferencesModifierBase;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public enum ChromePreferencesModifiers {
    ;

    public static PreferencesModifier downloadWithoutPrompt() {
        PreferencesModifier pm = new PreferencesModifierBase(PreferencesModifier.Type.CHROME_downloadWithoutPrompt, new Closure<Map<String, Object>>(null, null) {
            public Map<String, Object> doCall(Map<String, Object> preferences) {
                // Below two preference settings will disable popup dialog when download file
                preferences.put("profile.default_content_settings.popups", 0);
                preferences.put("download.prompt_for_download", false);
                return preferences;
            }

        });
        return pm;
    }

    public static PreferencesModifier downloadIntoUserHomeDownloadsDirectory() {
        Path p = Paths.get(System.getProperty("user.home"), "Downloads");
        return downloadIntoDirectory(p);
    }

    public static PreferencesModifier downloadIntoDirectory(final Path directory) {
        Objects.requireNonNull(directory);
        if (!Files.exists(directory)) {
            DefaultGroovyMethods.println(this, "created " + String.valueOf(directory));
            Files.createDirectories(directory);
        }

        PreferencesModifier pm = new PreferencesModifierBase(PreferencesModifier.Type.CHROME_downloadIntoDirectory, new Closure<Map<String, Object>>(null, null) {
            public Map<String, Object> doCall(Map<String, Object> preferences) {
                preferences.put("download.default_directory", directory.toString());
                return preferences;
            }

        });
        return pm;
    }

    public static PreferencesModifier disableViewersOfFlashAndPdf() {
        PreferencesModifier pm = new PreferencesModifierBase(PreferencesModifier.Type.CHROME_disableViewersOfFlashAndPdf, new Closure<Map<String, Object>>(null, null) {
            public Map<String, Object> doCall(Map<String, Object> preferences) {
                preferences.put("plugins.plugins_disabled", new ArrayList<String>(Arrays.asList("Adobe Flash Player", "Chrome PDF Viewer")));
                return preferences;
            }

        });
        return pm;
    }

}
