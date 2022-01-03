package com.kazurayam.webdriverfactory.desiredcapabilities

import org.junit.Test
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities

import groovy.json.JsonBuilder
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

import org.openqa.selenium.remote.DesiredCapabilities

class DesiredCapabilitiesBuilderImplTest {

    @Before
    void setup(){}

    @Test
    void test_build_withChromeOptions() {
        ChromeOptions options = new ChromeOptions()
        DesiredCapabilities dc = new DesiredCapabilitiesBuilderImpl().build(options)
        assertNotNull(dc)
    }
}
