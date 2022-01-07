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

    StackingWindowLayoutMetrics layoutMetrics
    int NUM_OF_WINDOWS = 3

    @Before
    void setup() {
        layoutMetrics = new StackingWindowLayoutMetrics.Builder(NUM_OF_WINDOWS)
                .windowDimension(new Dimension(1280, 1024)).build()
    }


    @Test
    void test_getWindowDimension() {
        assertEquals(1280, layoutMetrics.getWindowDimension(0).width)
        assertEquals(1024, layoutMetrics.getWindowDimension(0).height)
    }

    @Test
    void test_getWindowsPosition() {
        for (index in 0..2) {
            Point pos = layoutMetrics.getWindowPosition(index)
            assertEquals(layoutMetrics.getDisposition().x * index, pos.x)
            assertEquals(layoutMetrics.getDisposition().y * index, pos.y)
        }
    }

}
