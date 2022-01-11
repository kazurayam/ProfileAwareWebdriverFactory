package com.kazurayam.webdriverfactory.firefox

import com.kazurayam.webdriverfactory.UserProfile
import org.junit.Test

class FirefoxProfileUtilsTest {

    @Test
    void test_getFirefoxUserProfileList() {
        List<FirefoxUserProfile> list = FirefoxProfileUtils.getFirefoxUserProfileList()
        assert list.size() > 0

    }

    @Test
    void test_findFirefoxUserProfileOf() {
        UserProfile up = new UserProfile("default")
        Optional<FirefoxUserProfile> fup = FirefoxProfileUtils.findFirefoxUserProfileOf(up)
        assert fup.isPresent()
    }
}
