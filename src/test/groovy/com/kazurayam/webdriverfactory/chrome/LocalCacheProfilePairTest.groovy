package com.kazurayam.webdriverfactory.chrome

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail

class LocalCacheProfilePairTest {

    private final String cacheName = "Profile 17"
    private final String profileName = "Picasso"
    LocalCacheProfilePair cpp

    @Before
    void setup() {
        cpp = new LocalCacheProfilePair(cacheName, profileName)
    }

    @Test
    void testGetCacheName() {
        assertEquals(cacheName, cpp.getCacheName())
    }

    @Test
    void testGetProfileName() {
        assertEquals(profileName, cpp.getProfileName())
    }

    @Test
    void testEquals() {
        LocalCacheProfilePair other = new LocalCacheProfilePair(cacheName, profileName)
        assertEquals(other, cpp)
    }

    @Test
    void testHashCode() {
        LocalCacheProfilePair other = new LocalCacheProfilePair(cacheName, profileName)
        assertEquals(other.hashCode(), cpp.hashCode())
    }

    @Test
    void testToString() {
        String s = cpp.toString()
        assertEquals(String.format(
                "{\"cacheName\":\"%s\", \"profileName\":\"%s\"}",
                        cacheName, profileName),
                s)
    }

    @Test
    public void testInvalidCacheName() {
        try {
            cpp = new LocalCacheProfilePair("Foo", "Bar")
            fail("should fail with cacheName Foo, but actually passed")
        } catch (IllegalArgumentException e) {
            // as expected
        }
    }
}
