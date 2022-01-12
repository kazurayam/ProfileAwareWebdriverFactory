package com.kazurayam.webdriverfactory.firefox

import com.kazurayam.webdriverfactory.UserProfile
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.firefox.ProfilesIni

import java.nio.file.Path

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
    void test_openFF_with_existing_profile_using_FirefoxProfile_constructor() {
        Optional<FirefoxUserProfile> up = FirefoxProfileUtils.findFirefoxUserProfileOf(
                new UserProfile('Picasso'))
        assert up.isPresent()
        up.ifPresent({fup ->
            Path profileDir = fup.getProfileDirectory()
            println "profileDir => ${profileDir.toString()}"
            FirefoxProfile picasso = new FirefoxProfile(profileDir.toFile())
            openFF(picasso)
        })
    }

    @Test
    void test_openFF_with_existing_profile_using_ProfilesIni() {
        FirefoxProfile picasso = new ProfilesIni().getProfile("Picasso");
        openFF(picasso)
    }

    private void openFF(FirefoxProfile firefoxProfile) {
        FirefoxOptions options = new FirefoxOptions()
                .addPreference("browser.startup.page", 1)
                .addPreference("browser.startup.homepage", "https://www.google.co.uk")
                .addArguments("--width=800")
                .addArguments("--height=600")
                .setAcceptInsecureCerts(true)
                .setProfile(firefoxProfile)
                .setHeadless(false);
        // Initialize Firefox driver
        driver = new FirefoxDriver(options);
        driver.navigate().to("https://en.wikipedia.org/wiki/Pablo_Picasso")
    }
}
