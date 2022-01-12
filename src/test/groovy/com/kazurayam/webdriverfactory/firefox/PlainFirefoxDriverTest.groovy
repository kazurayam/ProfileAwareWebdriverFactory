package com.kazurayam.webdriverfactory.firefox

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.firefox.ProfilesIni

class PlainFirefoxDriverTest {

    FirefoxDriver driver

    @Before
    void setup() {
        driver = null
    }
    @After
    void teardown() {
        if (driver != null) {
            //driver.quit()
            driver = null
        }
    }

    @Test
    void test_openFF_anonymous() {
        driver = new FirefoxDriver()
        driver.navigate().to("https://www.google.com")
    }

    @Test
    void test_openFF_with_existing_profile() {
        FirefoxProfile picasso = new ProfilesIni().getProfile("Picasso");
        FirefoxOptions options = new FirefoxOptions()
                .addPreference("browser.startup.page", 1)
                .addPreference("browser.startup.homepage", "https://www.google.co.uk")
                .addArguments("--width=800")
                .addArguments("--height=600")
                .setAcceptInsecureCerts(true)
                .setProfile(picasso)
                .setHeadless(false);
        // Initialize Firefox driver
        driver = new FirefoxDriver(options);
        driver.navigate().to("https://en.wikipedia.org/wiki/Pablo_Picasso")
    }
}
