package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.ProfileDirectoryName
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.chrome.ChromeOptions

import java.nio.file.Path

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue
import groovy.json.*

class ChromeOptionsModifiersTest {

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
                ChromeOptionsModifiers.disableDevShmUsage().modify(options)
        verifyContainsArgument(modified, 'disable-dev-shm-usage')
    }

    @Test
    void test_disableExtensions() {
        ChromeOptions modified = ChromeOptionsModifiers.disableExtensions().modify(options)
        verifyContainsArgument(modified, 'disableExtensions')
    }

    @Test
    void test_disableGpu() {
        ChromeOptions modified =
                ChromeOptionsModifiers.disableGpu().modify(options)
        verifyContainsArgument(modified, 'disable-gpu')
    }

    @Test
    void test_disableInfobars() {
        ChromeOptions modified =
                ChromeOptionsModifiers.disableInfobars().modify(options)
        verifyContainsArgument(modified, 'disable-infobars')
    }

    @Test
    void test_headless() {
        ChromeOptions modified =
                ChromeOptionsModifiers.headless().modify(options)
        verifyContainsArgument(modified, '--headless')
    }

    @Test
    void test_incognito() {
        ChromeOptions modified =
                ChromeOptionsModifiers.incognito().modify(options)
        verifyContainsArgument(modified, '--incognito')
    }

    @Test
    void test_noSandbox() {
        ChromeOptions modified =
                ChromeOptionsModifiers.noSandbox().modify(options)
        verifyContainsArgument(modified, '--no-sandbox')
    }

    @Test
    void test_singleProcess() {
        ChromeOptions modified =
                ChromeOptionsModifiers.singleProcess().modify(options)
        verifyContainsArgument(modified, '--single-process')
    }


    @Test
    void test_windowSize1024_768() {
        ChromeOptions modified =
                ChromeOptionsModifiers.windowSize1024_768().modify(options)
        verifyContainsArgument(modified, 'window-size=1024,768')
    }

    @Test
    void test_windowSize() {
        ChromeOptions modified =
                ChromeOptionsModifiers.windowSize(800, 600).modify(options)
        verifyContainsArgument(modified, 'window-size=800,600')
    }

    @Test
    void test_withProfileDirectoryName() {
        Path userDataDir = ChromeProfileUtils.getDefaultUserDataDir()
        ProfileDirectoryName profileDirectoryName = new ProfileDirectoryName("Default")
        ChromeOptions modified =
                ChromeOptionsModifiers.withProfileDirectoryName(userDataDir, profileDirectoryName)
                        .modify(options)
        verifyContainsArgument(/**/modified, 'Default')
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
                    "--headless",
                    "user-data-dir=/Users/kazuakiurayama/Library/Application Support/Google/Chrome",
                    "profile-directory=Default"
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
        //
        boolean t = false
        for (String a in args) {
            if (a.contains(arg)) {
                t = true
                break
            }
        }
        assertTrue(
                String.format("arg=\"%s\" is not contained in the args=%s", arg, args.toString()),
                t)
    }
}
