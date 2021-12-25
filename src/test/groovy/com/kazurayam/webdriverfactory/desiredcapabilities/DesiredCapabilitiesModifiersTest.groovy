package com.kazurayam.webdriverfactory.desiredcapabilities

import groovy.json.JsonBuilder
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

import org.openqa.selenium.remote.DesiredCapabilities

class DesiredCapabilitiesModifiersTest {

    private DesiredCapabilities capabilities

    @Before
    void setup() {
        capabilities = new DesiredCapabilities()
    }

    @Test
    void test_browserName() {
        DesiredCapabilitiesModifier dcm =
                DesiredCapabilitiesModifiers.browserName("NyanNyan")
        DesiredCapabilities modified = dcm.modify(capabilities)
        verifyContainsCapability(modified, 'NyanNyan')
    }

    void verifyContainsCapability(DesiredCapabilities capabilities, String value) {
        Map<String, Object> map = capabilities.asMap()
        println new JsonBuilder(map).toPrettyString()
        /*
         * {
         *     "browserName": "NyanNyan"
         * }
         */
        assertTrue(map.values().contains(value))
    }
}
