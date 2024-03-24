package com.kazurayam.webdriverfactory.chrome

import org.junit.Before
import org.junit.Test
import static org.junit.Assert.assertEquals

class LocalCacheProfilePairsTest {

    private LocalCacheProfilePair instance0
    private LocalCacheProfilePairs pairs

    @Before
    void setup() {
        instance0 = new LocalCacheProfilePair("Profile 17", "Picasso")
        pairs = new LocalCacheProfilePairs()
        pairs.add(instance0)
    }

    @Test
    void test_size() {
        assertEquals(1, pairs.size())
    }

    @Test
    void test_get() {
        LocalCacheProfilePair cpp = pairs.get(0)
        assertEquals(instance0, cpp);
    }

}
