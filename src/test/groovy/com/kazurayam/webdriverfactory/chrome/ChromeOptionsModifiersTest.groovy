package com.kazurayam.webdriverfactory.chrome

import org.junit.Before
import org.junit.Test
import org.openqa.selenium.chrome.ChromeOptions

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue
import groovy.json.*

class ChromeOptionsModifiersTest {

    private ChromeOptions options

    @Before
    void setup() {
        options = new ChromeOptions()
    }

    @Test
    void test_headless() {
        ChromeOptionsModifier com = ChromeOptionsModifiers.headless()
        ChromeOptions modified = com.modify(options)
        verifyContainsArgument(modified, '--headless')
    }

    @Test
    void test_incognito() {
        ChromeOptionsModifier com = ChromeOptionsModifiers.incognito()
        ChromeOptions modified = com.modify(options)
        verifyContainsArgument(modified, '--incognito')
    }

    @Test
    void test_windowSize1024_768() {
        ChromeOptionsModifier com = ChromeOptionsModifiers.windowSize1024_768()
        ChromeOptions modified = com.modify(options)
        verifyContainsArgument(modified, 'window-size=1024,768')
    }

    @Test
    void test_windowSize() {
        ChromeOptionsModifier com = ChromeOptionsModifiers.windowSize(800, 600)
        ChromeOptions modified = com.modify(options)
        verifyContainsArgument(modified, 'window-size=800,600')
    }

    @Test
    void test_noSandbox() {
        ChromeOptionsModifier com = ChromeOptionsModifiers.noSandbox()
        ChromeOptions modified = com.modify(options)
        verifyContainsArgument(modified, '--no-sandbox')
    }

    @Test
    void test_disableInfobars() {
        ChromeOptionsModifier com = ChromeOptionsModifiers.disableInfobars()
        ChromeOptions modified = com.modify(options)
        verifyContainsArgument(modified, 'disable-infobars')
    }

    @Test
    void test_disableGpu() {
        ChromeOptionsModifier com = ChromeOptionsModifiers.disableGpu()
        ChromeOptions modified = com.modify(options)
        verifyContainsArgument(modified, 'disable-gpu')
    }

    @Test
    void test_disableDevShmUsage() {
        ChromeOptionsModifier com = ChromeOptionsModifiers.disableDevShmUsage()
        ChromeOptions modified = com.modify(options)
        verifyContainsArgument(modified, 'disable-dev-shm-usage')
    }

    @Test
    void test_disableExtensions() {
        ChromeOptionsModifier com = ChromeOptionsModifiers.disableExtensions()
        ChromeOptions modified = com.modify(options)
        verifyContainsArgument(modified, 'disableExtensions')
    }

    @Test
    void test_singleProcess() {
        ChromeOptionsModifier com = ChromeOptionsModifiers.singleProcess()
        ChromeOptions modified = com.modify(options)
        verifyContainsArgument(modified, '--single-process')
    }

    void verifyContainsArgument(ChromeOptions chromeOptions, String arg) {
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
        Map<String, Object> map2 = map1.get('goog:chromeOptions')
        assertNotNull(map2)
        assertTrue(map2.containsKey('args'))
        List<String> args = map2.get('args')
        assertNotNull(args)
        assertTrue(args.contains(arg))
    }
}
