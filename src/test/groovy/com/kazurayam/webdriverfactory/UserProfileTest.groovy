package com.kazurayam.webdriverfactory

import org.junit.Test

import static org.junit.Assert.*

class UserProfileTest {

    @Test
    void test_Smoke() {
        UserProfile smoke = new UserProfile("Smoke")
        assertEquals("Smoke", smoke.getName())
    }

    @Test
    void test_compareTo() {
        UserProfile upn1 = new UserProfile("1")
        UserProfile upn2 = new UserProfile("2")
        assertTrue((upn1 <=> upn2) < 0)
        assertTrue((upn2 <=> upn1) > 0)
        assertTrue((upn1 <=> upn1) == 0)
    }
}
