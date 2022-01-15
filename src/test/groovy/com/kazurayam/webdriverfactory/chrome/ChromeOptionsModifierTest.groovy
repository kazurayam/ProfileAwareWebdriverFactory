package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.ProfileDirectoryName
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.chrome.ChromeOptions

import java.nio.file.Path

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue
import groovy.json.*

class ChromeOptionsModifierTest {

    private ChromeOptions options
    private List<Object> arguments

    @Before
    void setup() {
        options = new ChromeOptions()
        arguments = Arrays.asList()
    }

    @Test
    void test_disableDevShmUsage() {
        ChromeOptions modified =
                ChromeOptionsModifiers.disableDevShmUsage.apply(options, arguments)
        verifyContainsArgument(modified, 'disable-dev-shm-usage')
    }

    @Test
    void test_disableExtensions() {
        ChromeOptions modified = ChromeOptionsModifiers.disableExtensions.apply(options, arguments)
        verifyContainsArgument(modified, 'disableExtensions')
    }

    @Test
    void test_disableGpu() {
        ChromeOptions modified =
                ChromeOptionsModifiers.disableGpu.apply(options, arguments)
        verifyContainsArgument(modified, 'disable-gpu')
    }

    @Test
    void test_disableInfobars() {
        ChromeOptions modified =
                ChromeOptionsModifiers.disableInfobars.apply(options, arguments)
        verifyContainsArgument(modified, 'disable-infobars')
    }

    @Test
    void test_headless() {
        ChromeOptions modified =
                ChromeOptionsModifiers.headless.apply(options, arguments)
        verifyContainsArgument(modified, '--headless')
    }

    @Test
    void test_incognito() {
        ChromeOptions modified =
                ChromeOptionsModifiers.incognito.apply(options, arguments)
        verifyContainsArgument(modified, '--incognito')
    }

    @Test
    void test_noSandbox() {
        ChromeOptions modified =
                ChromeOptionsModifiers.noSandbox.apply(options, arguments)
        verifyContainsArgument(modified, '--no-sandbox')
    }

    @Test
    void test_singleProcess() {
        ChromeOptions modified =
                ChromeOptionsModifiers.singleProcess.apply(options, arguments)
        verifyContainsArgument(modified, '--single-process')
    }


    @Test
    void test_windowSize1024_768() {
        ChromeOptions modified =
                ChromeOptionsModifiers.windowSize1024_768.apply(options, arguments)
        verifyContainsArgument(modified, 'window-size=1024,768')
    }

    @Test
    void test_windowSize() {
        arguments = Arrays.asList(800, 600)
        ChromeOptions modified =
                ChromeOptionsModifiers.windowSize.apply(options, arguments)
        verifyContainsArgument(modified, 'window-size=800,600')
    }

    @Test
    void test_withProfileDirectoryName() {
        Path userDataDir = ChromeProfileUtils.getDefaultUserDataDir();
        ProfileDirectoryName profileDirectoryName = new ProfileDirectoryName("Default");
        arguments = Arrays.asList(userDataDir, profileDirectoryName)
        ChromeOptions modified =
                ChromeOptionsModifiers.withProfileDirectoryName(options, arguments)
        verifyContainsArgument(modified, 'Default')
    }

    /**
     *
     * @param chromeOptions
     * @param arg
     */
    private static void verifyContainsArgument(ChromeOptions chromeOptions, String arg) {
        Objects.requireNonNull(chromeOptions)
        Map<String, Object> map1 = chromeOptions.toJson()
        println new JsonBuilder(map1).toPrettyString()
        /* map1:
        {
            "browserName": "chrome",
            "goog:chromeOptions": {
                "args": [
                    "--headless"
                ],
                "extensions": [
                ]
            }
        }
        */
        assertNotNull(map1)
        assertTrue(map1.containsKey('goog:chromeOptions'))
        Map<String, Object> map2 = (Map)map1.get('goog:chromeOptions')
        assertNotNull(map2)
        assertTrue(map2.containsKey('args'))
        List<String> args = (List)map2.get('args')
        assertNotNull(args)
        assertTrue(args.contains(arg))
    }
}
