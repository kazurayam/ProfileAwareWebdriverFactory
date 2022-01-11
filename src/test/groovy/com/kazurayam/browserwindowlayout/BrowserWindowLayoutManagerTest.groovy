package com.kazurayam.browserwindowlayout

import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4.class)
class BrowserWindowLayoutManagerTest {

    TilingWindowLayoutMetrics tilingLayout
    StackingWindowLayoutMetrics stackingLayout
    LaunchedChromeDriver launched

    @Before
    void setup() {
        tilingLayout = new TilingWindowLayoutMetrics.Builder(4).build()
        stackingLayout = new StackingWindowLayoutMetrics.Builder(3).build()
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        launched = factory.newChromeDriver()
    }

    @After
    void teardown() {
        if (launched != null) {
            launched.getDriver().quit()
            launched = null
        }
    }

    @Test
    void test_tiling() {
        String url = "http://example.com/"
        launched.getDriver().navigate().to(url)
        for (int index in 0..<tilingLayout.getSize()) {
            BrowserWindowLayoutManager.layout(
                    launched.getDriver(),
                    tilingLayout.getWindowPosition(index),
                    tilingLayout.getWindowDimension(index)
            )
        }
    }

    @Test
    void test_stacking() {
        String url = "http://example.com/"
        launched.getDriver().navigate().to(url)
        for (int index in 0..<stackingLayout.getSize()) {
            BrowserWindowLayoutManager.layout(
                    launched.getDriver(),
                    stackingLayout.getWindowPosition(index),
                    stackingLayout.getWindowDimension(index)
            )
        }
    }

}
