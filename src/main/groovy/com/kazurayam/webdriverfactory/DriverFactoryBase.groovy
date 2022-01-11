package com.kazurayam.webdriverfactory

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

import java.time.Duration
import java.util.concurrent.TimeUnit

abstract class DriverFactoryBase {

    protected Integer pageLoadTimeoutSeconds

    void setPageLoadTimeout(Integer waitSeconds) {
        Objects.requireNonNull(waitSeconds)
        if (waitSeconds <= 0) {
            throw new IllegalArgumentException("waitSeconds=${waitSeconds} must not be <=0")
        }
        if (waitSeconds > 999) {
            throw new IllegalArgumentException("waitSeconds=${waitSeconds} must not be > 999")
        }
        this.pageLoadTimeoutSeconds = waitSeconds
    }

    static protected void applyPageLoadingTimeout(WebDriver driver, Integer seconds) {
        if (seconds != Integer.MIN_VALUE) {
            Duration dur = Duration.ofSeconds((long)seconds)
            long millis = dur.toMillis()
            driver.manage().timeouts().pageLoadTimeout(millis, TimeUnit.MILLISECONDS);
        }
    }

}
