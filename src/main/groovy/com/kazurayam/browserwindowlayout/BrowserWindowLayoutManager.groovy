package com.kazurayam.browserwindowlayout

import org.openqa.selenium.Dimension
import org.openqa.selenium.Point
import org.openqa.selenium.WebDriver

class BrowserWindowLayoutManager {

    static void layout(WebDriver driver, Point position, Dimension dimension) {
        Objects.requireNonNull(driver)
        Objects.requireNonNull(position)
        Objects.requireNonNull(dimension)
        // move the browser window to this position (x,y)
        driver.manage().window().setPosition(position)
        // resize the browser window to this dimension (width, height)
        driver.manage().window().setSize(dimension)
    }
}
