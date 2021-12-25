package com.kazurayam.webdriverfactory.chrome

import org.junit.Test
import static org.junit.Assert.*

class ProfileDirectoryNameTest {

    @Test
    void test_smoke() {
        ProfileDirectoryName profile1 = new ProfileDirectoryName("Profile 1")
        assertEquals("Profile 1", profile1.getName())
    }
}
