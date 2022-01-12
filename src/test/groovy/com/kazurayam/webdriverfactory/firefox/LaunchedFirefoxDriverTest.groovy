package com.kazurayam.webdriverfactory.firefox

import com.kazurayam.webdriverfactory.UserProfile
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.firefox.FirefoxDriver

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import static org.junit.Assert.*

class LaunchedFirefoxDriverTest {

    static Path outputFolder
    FirefoxDriver driver

    @BeforeClass
    static void beforeClass() {
        WebDriverManager.chromedriver().setup()
        outputFolder = Paths.get(".").resolve("build/tmp/testOutput")
                .resolve(LaunchedFirefoxDriverTest.class.getSimpleName())
        Files.createDirectories(outputFolder)
    }

    @After
    void tearDown() {
        if (driver != null) {
            driver.quit()
            driver = null
        }
    }

    @Test
    void test_smoke() {
        driver = new FirefoxDriver()
        LaunchedFirefoxDriver launched =
                new LaunchedFirefoxDriver(driver)
        assertNotNull(launched)
        Optional<FirefoxUserProfile> opt = FirefoxProfileUtils.findFirefoxUserProfileOf(new UserProfile("default"))
        assert opt.isPresent()
        FirefoxUserProfile firefoxUserProfile
        opt.ifPresent({it ->
            launched.setFirefoxUserProfile(it)
        })
        //
        assert launched.getFirefoxUserProfile().isPresent()
        launched.getFirefoxUserProfile().ifPresent(
                { it ->
                    println "FirefoxUserProfile => " + it.toString()})
        //assert launched.getEmployedOptions().isPresent()
        //launched.getEmployedOptions().ifPresent({ it ->
        //    println "options => " + it.toString()
        //})
    }
}
