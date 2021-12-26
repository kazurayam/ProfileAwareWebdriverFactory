package com.kazurayam.webdriverfactory.chrome

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class ChromePreferencesModifiers {

    static ChromePreferencesModifier downloadWithoutPrompt() {
        ChromePreferencesModifier pm = new Base({ Map<String, Object> preferences ->
            // Below two preference settings will disable popup dialog when download file
            preferences.put('profile.default_content_settings.popups', 0)
            preferences.put('download.prompt_for_download', false)
            return preferences
        })
        return pm
    }

    static ChromePreferencesModifier downloadIntoUserHomeDownloadsDirectory() {
        Path p = Paths.get(System.getProperty('user.home'), 'Downloads')
        return downloadIntoDirectory(p)
    }

    static ChromePreferencesModifier downloadIntoDirectory(Path directory) {
        Objects.requireNonNull(directory)
        if (!Files.exists(directory)) {
            println "created ${directory}"
            Files.createDirectories(directory)
        }
        ChromePreferencesModifier pm = new Base({ Map<String, Object> preferences ->
            preferences.put('download.default_directory', directory.toString())
            return preferences
        })
        return pm
    }

    static ChromePreferencesModifier disableViewersOfFlashAndPdf() {
        ChromePreferencesModifier pm = new Base({ Map<String, Object> preferences ->
            preferences.put('plugins.plugins_disabled', [
                    'Adobe Flash Player',
                    'Chrome PDF Viewer'
            ])
            return preferences
        })
        return pm
    }

    /**
     *
     */
    private static class Base implements ChromePreferencesModifier {
        private Closure closure
        Base(Closure closure) {
            this.closure = closure
        }
        @Override
        Map<String, Object> modify(Map<String, Object> preferences) {
            Objects.requireNonNull(preferences)
            return (Map)closure.call(preferences)
        }
    }

    private ChromePreferencesModifiers() {}
}
