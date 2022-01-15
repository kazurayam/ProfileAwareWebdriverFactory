package com.kazurayam.webdriverfactory.chrome


import java.nio.file.Path
import java.nio.file.Paths

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

class ChromePreferencesModifiersTest {

    private Map<String, Object> preferences

    @Before
    void setup() {
        preferences = new HashMap<>()
    }

    @Test
    void test_downloadWithoutPrompt() {
        PreferencesModifier pm = ChromePreferencesModifiers.downloadWithoutPrompt()
        Map<String, Object> modified = pm.modify(preferences)
        assertEquals(0, modified.get('profile.default_content_settings.popups'))
        assertEquals(false, modified.get('download.prompt_for_download'))
    }

    @Test
    void test_downloadIntoUserHomeDownloadsDirectory() {
        PreferencesModifier pm = ChromePreferencesModifiers.downloadIntoUserHomeDownloadsDirectory()
        Map<String, Object> modified = pm.modify(preferences)
        assertNotNull(modified.get('download.default_directory'))
        String value = (String)modified.get('download.default_directory')
        assertTrue(value.endsWith('Downloads'))
    }

    @Test
    void test_downloadIntoDirectory() {
        Path dir = Paths.get(System.getProperty('user.dir'), 'tmp')
        PreferencesModifier pm = ChromePreferencesModifiers.downloadIntoDirectory(dir)
        Map<String, Object> modified = pm.modify(preferences)
        assertNotNull(modified.get('download.default_directory'))
        String value = (String)modified.get('download.default_directory')
        assertTrue(value.endsWith('tmp'))
    }

    @Test
    void test_disableViewersOfFlashAndPdf() {
        PreferencesModifier pm = ChromePreferencesModifiers.disableViewersOfFlashAndPdf()
        Map<String, Object> modified = pm.modify(preferences)
        assertNotNull(modified.get('plugins.plugins_disabled'))
        String value = (String)modified.get('plugins.plugins_disabled')
        assertTrue(value.contains('Adobe Flash Player'))
        assertTrue(value.contains('Chrome PDF Viewer'))
    }
}
