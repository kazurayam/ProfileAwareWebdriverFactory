package com.kazurayam.browserwindowlayout

import static org.junit.Assert.*

import org.openqa.selenium.Dimension
import org.openqa.selenium.Point

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4.class)
class StackingWindowLayoutMetricsTest {

    StackingWindowLayoutMetrics lm
    int NUM_OF_WINDOWS = 3

    @Before
    void setup() {
        lm = new StackingWindowLayoutMetrics.Builder().windowDimension(new Dimension(1280, 1024)).build()
    }


    @Test
    void test_getWindowDimension() {
        WindowLocation windowLocation = new WindowLocation(NUM_OF_WINDOWS,0)
        assertTrue("width=${lm.getWindowDimension(windowLocation).width}, expected 1280",
                lm.getWindowDimension(windowLocation).width == 1280)
        assertTrue("height=${lm.getWindowDimension(windowLocation).height}, epected 1024",
                lm.getWindowDimension(windowLocation).height == 1024)
    }

    @Test
    void testWindow0() {
        WindowLocation windowLocation = new WindowLocation(NUM_OF_WINDOWS,0)
        Point pos = lm.getWindowPosition(windowLocation)
        assertTrue("pos.x=${pos.x}, expected to be equal to ${lm.getDisposition().width * 0}",
                pos.x == lm.getDisposition().width * 0)
        assertTrue("pos.y=${pos.y}, expected to be equal to ${lm.getDisposition().height * 0}",
                pos.y == lm.getDisposition().height * 0)
    }

    @Test
    void testWindow1() {
        WindowLocation windowLocation = new WindowLocation(NUM_OF_WINDOWS,1)
        Point pos = lm.getWindowPosition(windowLocation)
        assertTrue("pos.x=${pos.x}, expected to be equal to ${lm.getDisposition().width * 1}",
                pos.x == lm.getDisposition().width * 1)
        assertTrue("pos.y=${pos.y}, expected to be equal to ${lm.getDisposition().height * 1}",
                pos.y== lm.getDisposition().height * 1)
    }

    @Test
    void testWindow2() {
        WindowLocation windowLocation = new WindowLocation(NUM_OF_WINDOWS,2)
        Point pos = lm.getWindowPosition(windowLocation)
        assertTrue("pos.x=${pos.x}, expected to be equal to ${lm.getDisposition().width * 2}",
                pos.x == lm.getDisposition().width * 2)
        assertTrue("pos.y=${pos.y}, expected to be equal to ${lm.getDisposition().height * 2}",
                pos.y == lm.getDisposition().height * 2)
    }

}
