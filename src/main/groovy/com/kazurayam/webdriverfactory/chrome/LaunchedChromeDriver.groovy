package com.kazurayam.webdriverfactory.chrome

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import groovy.json.JsonOutput

class LaunchedChromeDriver {

    private final ChromeDriver driver
    private Optional<ChromeUserProfile> chromeUserProfile
    private Optional<ChromeDriverFactory.UserDataAccess> instruction
    private Optional<ChromeOptions> employedOptions

    LaunchedChromeDriver(ChromeDriver driver) {
        Objects.requireNonNull(driver)
        this.driver = driver
        this.chromeUserProfile = Optional.empty()
        this.instruction = Optional.empty()
        this.employedOptions = Optional.empty()
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

    LaunchedChromeDriver setEmployedOptions(ChromeOptions options) {
        Objects.requireNonNull(options)
        this.employedOptions = Optional.of(options)
        return this
    }

    Optional<ChromeOptions> getEmployedOptions() {
        return this.employedOptions
    }

    Optional<String> getEmployedOptionsAsJSON() {
        String json = ""
        this.getEmployedOptions().ifPresent(
                { ChromeOptions options ->
                    json = JsonOutput.prettyPrint(JsonOutput.toJson(options.toJson()))
                })
        return Optional.of(json)
    }
}
