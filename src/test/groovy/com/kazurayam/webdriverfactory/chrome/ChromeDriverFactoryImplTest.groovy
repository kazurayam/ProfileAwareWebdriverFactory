package com.kazurayam.webdriverfactory.chrome

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.BeforeClass
import org.junit.Test
import static org.junit.Assert.assertNotNull
import org.openqa.selenium.chrome.ChromeOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static org.junit.Assert.assertTrue

class ChromeDriverFactoryImplTest {

    static Logger logger = LoggerFactory.getLogger(ChromeDriverFactoryImplTest.class);
    private static ObjectMapper mapper;

    @BeforeClass
    static void beforeClass() {
        mapper = new ObjectMapper();
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
        logger.info("[test_applyChromeOptionsModifiers] options: " + toJson(options));
        assertTrue("options should contain --incognito, but actually not",
                toJson(options).contains("--incognito"));

        // when
        ChromeOptions result =
                ChromeDriverFactoryImpl.applyChromeOptionsModifiers(options, modifiers);
        logger.info("[test_applyChromeOptionsModifiers] result: " + toJson(result));
        // then
        assertTrue("disable-dev-shm-usage option is missing",
                toJson(result).contains("disable-dev-shm-usage"))
        assertTrue("--incognito option is missing", toJson(result).contains("--incognito"))
    }

    /**
     *
     * @param options
     * @return
     */
    String toJson(ChromeOptions options) {
        return ChromeOptionsUtil.toJson(options);
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
