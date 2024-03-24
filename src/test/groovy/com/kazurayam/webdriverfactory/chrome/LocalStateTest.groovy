package com.kazurayam.webdriverfactory.chrome

import org.junit.Before
import org.junit.Test

import java.nio.file.Path
import java.nio.file.Paths

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

class LocalStateTest {

    private LocalState ls;

    @Before
    void setup() {
        ls = new LocalState(getLocalStateText())
    }

    @Test
    void test_size() {
        assertEquals(2, ls.size())
    }

    @Test
    void test_lookupCacheName_Default() {
        assertEquals("Default", ls.lookupCacheNameOf("Kazuaki"));
    }

    @Test
    void test_lookupCacheName_Profile17() {
        assertEquals("Profile 17", ls.lookupCacheNameOf("Picasso"));
    }

    @Test
    void test_lookupCacheName_notfound() {
        assertNull(ls.lookupCacheNameOf("Renoir"))
    }

    private String getLocalStateText() {
        return '''{
            "profile": {
                 "info_cache": {
                       "Default": {
                             "name": "Kazuaki"
                       },
                       "Profile 17": {
                             "name": "Picasso"
                       }
                 }
             }
         }'''
    }

    private Path getLocalState() {
        Path home = Paths.get(System.getProperty("user.home"))
        Path chromeInstalledDir =
                home.resolve("Library/Application Support/Google/Chrome")
        return chromeInstalledDir.resolve("Local State")
    }

    /**
     * This test depends on the Chrome environment on the platform.
     * This test will run ok only on the kazurayam's Macbook Air.
     * Should put @Ignore when it disturbs you.
     */
    @Test
    void test_real_Chrome_Local_State() {
        LocalState localState = new LocalState(getLocalState())
        assertEquals("Profile 17", ls.lookupCacheNameOf("Picasso"));
        assertEquals("Default", ls.lookupCacheNameOf("Kazuaki"));
    }
}