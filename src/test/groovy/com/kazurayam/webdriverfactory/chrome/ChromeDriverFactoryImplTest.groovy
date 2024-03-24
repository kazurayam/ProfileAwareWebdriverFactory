package com.kazurayam.webdriverfactory.chrome

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.BeforeClass
import org.junit.Test
import static org.junit.Assert.assertNotNull
import org.openqa.selenium.chrome.ChromeOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.junit.Assert.assertTrue

class ChromeDriverFactoryImplTest {

    static Logger logger = LoggerFactory.getLogger(ChromeDriverFactoryImplTest.class);
    private static Gson gson;

    @BeforeClass
    static void beforeClass() {
        gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
    }

    @Test
    void test_applyChromePreferencesModifiers() {
        // prepare
        Set<ChromePreferencesModifier> modifiers = new HashSet<>()
        modifiers.add(ChromePreferencesModifiers.downloadWithoutPrompt())
        Map<String, Object> preference = new HashMap<>()
        preference.put("plugins.plugins_disabled",
                Arrays.asList("Adobe Flash Player", "Chrome PDF Viewer"))
        // when
        Map<String, Object> result =
                ChromeDriverFactoryImpl.applyChromePreferencesModifiers(preference, modifiers)
        // then
        assertTrue(result.containsKey("profile.default_content_settings.popups"));
        assertTrue(result.containsKey("plugins.plugins_disabled"));
    }

    @Test
    void test_applyChromeOptionsModifiers() {
        // prepare
        Set<ChromeOptionsModifier> modifiers = new HashSet<>();
        modifiers.add(ChromeOptionsModifiers.disableDevShmUsage());
        logger.info("[test_applyChromeOptionsModifiers] modifiers: " + modifiers);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        logger.info("[test_applyChromeOptionsModifiers] options: " +
                gson.toJson(options.asMap()));
        assertTrue("options should contain --incognito, but actually not",
                gson.toJson(options.asMap()).contains("--incognito"));

        // when
        ChromeOptions result =
                ChromeDriverFactoryImpl.applyChromeOptionsModifiers(options, modifiers);
        logger.info("[test_applyChromeOptionsModifiers] result: " +
                gson.toJson(result.asMap()));
        // then
        assertTrue("disable-dev-shm-usage option is missing",
                gson.toJson(result.asMap()).contains("disable-dev-shm-usage"))
        assertTrue("--incognito option is missing",
                gson.toJson(result.asMap()).contains("--incognito"))
    }

    @Test
    void test_buildOptions() {
        Set<ChromePreferencesModifier> chromePreferencesModifiers = new HashSet<>()
        chromePreferencesModifiers.add(ChromePreferencesModifiers.downloadWithoutPrompt())
        //
        Set<ChromeOptionsModifier> chromeOptionsModifiers = new HashSet<>()
        chromeOptionsModifiers.add(ChromeOptionsModifiers.disableDevShmUsage())
        //
        ChromeOptions chromeOptions =
                ChromeDriverFactoryImpl.buildOptions(
                        chromePreferencesModifiers,
                        chromeOptionsModifiers);
        assertNotNull(chromeOptions);
    }

}
