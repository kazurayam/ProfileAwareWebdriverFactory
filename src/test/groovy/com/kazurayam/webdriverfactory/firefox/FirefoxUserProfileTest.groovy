package com.kazurayam.webdriverfactory.firefox

import com.kazurayam.webdriverfactory.UserProfile
import groovy.json.JsonSlurper
import org.junit.Before
import org.junit.Test

import java.nio.file.Files
import java.nio.file.Path

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class FirefoxUserProfileTest {

    private FirefoxUserProfile profile

    @Before
    void setup() {
        Optional<FirefoxUserProfile> fup =
                FirefoxProfileUtils.findFirefoxUserProfileOf(new UserProfile("default"))
        assert fup.isPresent()
        fup.ifPresent({it ->
            profile = it
        })
    }

    @Test
    void test_FirefoxUserProfile() {
        assertNotNull(profile)
        assertNotNull(profile.getUserProfile())
        Path userDataDir = FirefoxProfileUtils.getDefaultUserDataDir()
        assertEquals(userDataDir, profile.getUserDataDir())
        Path userProfileDirectory = profile.getProfileDirectory()
        assert Files.exists(userProfileDirectory)
    }

    @Test
    void test_toString() {
        println profile.toString()
        JsonSlurper slurper = new JsonSlurper()
        def obj = slurper.parseText(profile.toString())
        assert obj != null
    }
}
