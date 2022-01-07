package com.kazurayam.browserwindowlayout

import com.kazurayam.webdriverfactory.WebDriverFactory
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.openqa.selenium.WebDriver

@RunWith(JUnit4.class)
class BrowserWindowLayoutManagerTest {

    TilingWindowLayoutMetrics tilingLayout
    StackingWindowLayoutMetrics stackingLayout
    WebDriver driver

    @Before
    void setup() {
        tilingLayout = new TilingWindowLayoutMetrics.Builder(4).build()
        stackingLayout = new StackingWindowLayoutMetrics.Builder(3).build()
        WebDriverFactory factory = new WebDriverFactory.Builder().build()   // will use Chrome driver as default
        driver = factory.newWebDriver()
    }

    @After
    void teardown() {
        if (driver != null) {
            driver.quit()
            driver = null
        }
    }

    @Test
    void test_tiling() {
        String url = "http://example.com/"
        driver.navigate().to(url)
        for (int index in 0..<tilingLayout.getSize()) {
            BrowserWindowLayoutManager.layout(
                    driver,
                    tilingLayout.getWindowPosition(index),
                    tilingLayout.getWindowDimension(index)
            )
        }
    }

    @Test
    void test_stacking() {
        String url = "http://example.com/"
        driver.navigate().to(url)
        for (int index in 0..<stackingLayout.getSize()) {
            BrowserWindowLayoutManager.layout(
                    driver,
                    stackingLayout.getWindowPosition(index),
                    stackingLayout.getWindowDimension(index)
            )
        }
    }

}
