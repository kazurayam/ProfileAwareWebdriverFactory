package com.kazurayam.webdriverfactory.chrome

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail

class CacheProfilePairTest {

    private final String cacheName = "Profile 17"
    private final String profileName = "Picasso"
    CacheProfilePair cpp

    @Before
    void setup() {
        cpp = new CacheProfilePair(cacheName, profileName)
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
        CacheProfilePair other = new CacheProfilePair(cacheName, profileName)
        assertEquals(other, cpp)
    }

    @Test
    void testHashCode() {
        CacheProfilePair other = new CacheProfilePair(cacheName, profileName)
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
            cpp = new CacheProfilePair("Foo", "Bar")
            fail("should fail with cacheName Foo, but actually passed")
        } catch (IllegalArgumentException e) {
            // as expected
        }
    }
}
