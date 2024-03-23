package com.kazurayam.webdriverfactory.chrome

import org.junit.Before
import org.junit.Test
import static org.junit.Assert.assertEquals

class CacheProfilePairsTest {

    private CacheProfilePair instance0
    private CacheProfilePairs pairs

    @Before
    void setup() {
        instance0 = new CacheProfilePair("Profile 17", "Picasso")
        pairs = new CacheProfilePairs()
        pairs.add(instance0)
    }

    @Test
    void test_size() {
        assertEquals(1, pairs.size())
    }

    @Test
    void test_get() {
        CacheProfilePair cpp = pairs.get(0)
        assertEquals(instance0, cpp);
    }

}
