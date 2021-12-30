package com.kazurayam.browserwindowlayout


import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4.class)
class WindowLocationTest {

    @Before
    void setup() {
    }

    @Test
    void test_validate_pass() {
        try {
            WindowLocation.validate(1,0)
        } catch (IllegalArgumentException e) {
            fail("should not raise Exception")
        }
    }

    @Test
    void test_validate_size_0() {
        try {
            WindowLocation.validate(0,0)
            fail("should raise Exception when size=0")
        } catch (IllegalArgumentException e) {
            ;
        }
    }

    @Test
    void test_validate_minus_index() {
        try {
            WindowLocation.validate(1,-1)
            fail("should raise Exception when index=-1")
        } catch (IllegalArgumentException e) {
            ;
        }
    }

    @Test
    void test_validate_index_equal_to_size() {
        try {
            WindowLocation.validate(1,1)
            fail("should raise Exception when index=1 when size=1")
        } catch (IllegalArgumentException e) {
            ;
        }
    }

    @Test
    void test_normal() {
        WindowLocation bwl = new WindowLocation(3, 0)
        assertEquals(3, bwl.size)
        assertEquals(0, bwl.index)
    }

}
