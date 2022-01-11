package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.PreferencesModifier
import com.kazurayam.webdriverfactory.PreferencesModifierBase

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

enum ChromePreferencesModifiers {

    static PreferencesModifier downloadWithoutPrompt() {
        PreferencesModifier pm = new PreferencesModifierBase(
                PreferencesModifier.Type.CHROME_downloadWithoutPrompt,
                { Map<String, Object> preferences ->
                    // Below two preference settings will disable popup dialog when download file
                    preferences.put('profile.default_content_settings.popups', 0)
                    preferences.put('download.prompt_for_download', false)
                    return preferences
                })
        return pm
    }

    static PreferencesModifier downloadIntoUserHomeDownloadsDirectory() {
        Path p = Paths.get(System.getProperty('user.home'), 'Downloads')
        return downloadIntoDirectory(p)
    }

    static PreferencesModifier downloadIntoDirectory(Path directory) {
        Objects.requireNonNull(directory)
        if (!Files.exists(directory)) {
            println "created ${directory}"
            Files.createDirectories(directory)
        }
        PreferencesModifier pm = new PreferencesModifierBase(
                PreferencesModifier.Type.CHROME_downloadIntoDirectory,
                { Map<String, Object> preferences ->
                    preferences.put('download.default_directory', directory.toString())
                    return preferences
                })
        return pm
    }

    static PreferencesModifier disableViewersOfFlashAndPdf() {
        PreferencesModifier pm = new PreferencesModifierBase(
                PreferencesModifier.Type.CHROME_disableViewersOfFlashAndPdf,
                { Map<String, Object> preferences ->
                    preferences.put('plugins.plugins_disabled', [
                            'Adobe Flash Player',
                            'Chrome PDF Viewer'
                    ])
                    return preferences
                })
        return pm
    }

}
