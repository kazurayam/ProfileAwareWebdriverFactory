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
        tilingLayout = new TilingWindowLayoutMetrics.Builder().build()
        stackingLayout = new StackingWindowLayoutMetrics.Builder().build()
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
        List<WindowLocation> locations = [
                new WindowLocation(2, 0),
                new WindowLocation(2, 1),
                new WindowLocation(4, 0),
                new WindowLocation(4, 1),
                new WindowLocation(4, 2),
                new WindowLocation(4, 3),
        ]
        locations.each { loc ->
            BrowserWindowLayoutManager.layout(
                    driver,
                    tilingLayout.getWindowPosition(loc),
                    tilingLayout.getWindowDimension(loc))
        }
    }

    @Test
    void test_stacking() {
        String url = "http://demoaut-mimic.kazurayam.com/"
        driver.navigate().to(url)
        List<WindowLocation> locations = [
                new WindowLocation(4, 0),
                new WindowLocation(4, 1),
                new WindowLocation(4, 2),
                new WindowLocation(4, 3),
        ]
        locations.each { loc ->
            BrowserWindowLayoutManager.layout(
                    driver,
                    stackingLayout.getWindowPosition(loc),
                    tilingLayout.getWindowDimension(loc))
        }
    }

}
