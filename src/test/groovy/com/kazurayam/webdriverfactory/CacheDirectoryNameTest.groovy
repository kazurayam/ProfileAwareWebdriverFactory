package com.kazurayam.webdriverfactory

import org.junit.Test
import static org.junit.Assert.*

class CacheDirectoryNameTest {

    @Test
    void test_smoke() {
        CacheDirectoryName profile1 = new CacheDirectoryName("Profile 1")
        assertEquals("Profile 1", profile1.getName())
    }
}
