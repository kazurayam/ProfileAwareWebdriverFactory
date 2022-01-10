package com.kazurayam.webdriverfactory.chrome

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.DesiredCapabilities
import groovy.json.JsonOutput

class LaunchedChromeDriver {

    private final ChromeDriver driver
    private Optional<ChromeUserProfile> chromeUserProfile
    private Optional<ChromeDriverFactory.UserDataAccess> instruction
    private Optional<DesiredCapabilities> employedDesiredCapabilities

    LaunchedChromeDriver(ChromeDriver driver) {
        Objects.requireNonNull(driver)
        this.driver = driver
        this.chromeUserProfile = Optional.empty()
        this.instruction = Optional.empty()
        this.employedDesiredCapabilities = Optional.empty()
    }

    ChromeDriver getDriver() {
        return this.driver
    }

    LaunchedChromeDriver setChromeUserProfile(ChromeUserProfile cup) {
        Objects.requireNonNull(cup)
        this.chromeUserProfile = Optional.of(cup)
        return this
    }

    Optional<ChromeUserProfile> getChromeUserProfile() {
        return this.chromeUserProfile
    }

    LaunchedChromeDriver setInstruction(ChromeDriverFactory.UserDataAccess instruction) {
        Objects.requireNonNull(instruction)
        this.instruction = Optional.of(instruction)
        return this
    }

    Optional<ChromeDriverFactory.UserDataAccess> getInstruction() {
        return this.instruction
    }

    LaunchedChromeDriver setEmployedDesiredCapabilities(DesiredCapabilities desiredCapabilities) {
        Objects.requireNonNull(desiredCapabilities)
        this.employedDesiredCapabilities = Optional.of(desiredCapabilities)
        return this
    }

    Optional<DesiredCapabilities> getEmployedDesiredCapabilities() {
        return this.employedDesiredCapabilities
    }

    Optional<String> getEmployedDesiredCapabilitiesAsJSON() {
        String json = ""
        this.getEmployedDesiredCapabilities().ifPresent(
                { DesiredCapabilities dc ->
                    //json = JsonOutput.prettyPrint(JsonOutput.toJson(dc))
                    json = JsonOutput.prettyPrint(JsonOutput.toJson(dc.toJson()))
                })
        return Optional.of(json)
    }
}
