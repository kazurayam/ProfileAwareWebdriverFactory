package com.kazurayam.webdriverfactory.firefox

import groovy.json.JsonOutput
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions

class LaunchedFirefoxDriver {

    private final FirefoxDriver driver
    private Optional<FirefoxUserProfile> firefoxUserProfile
    private Optional<FirefoxDriverFactory.UserDataAccess> instruction
    private Optional<FirefoxOptions> employedOptions

    LaunchedFirefoxDriver(FirefoxDriver driver) {
        Objects.requireNonNull(driver)
        this.driver = driver
        this.firefoxUserProfile = Optional.empty()
        this.instruction = Optional.empty()
        this.employedOptions = Optional.empty()
    }

    FirefoxDriver getDriver() {
        return this.driver
    }

    LaunchedFirefoxDriver setFirefoxUserProfile(FirefoxUserProfile fup) {
        Objects.requireNonNull(fup)
        this.firefoxUserProfile = Optional.of(fup)
        return this
    }

    Optional<FirefoxUserProfile> getFirefoxUserProfile() {
        return this.firefoxUserProfile
    }

    LaunchedFirefoxDriver setInstruction(FirefoxDriverFactory.UserDataAccess instruction) {
        Objects.requireNonNull(instruction)
        this.instruction = Optional.of(instruction)
        return this
    }

    Optional<FirefoxDriverFactory.UserDataAccess> getInstruction() {
        return this.instruction
    }

    LaunchedFirefoxDriver setEmployedOptions(FirefoxOptions options) {
        Objects.requireNonNull(options)
        this.employedOptions = Optional.of(options)
        return this
    }

    Optional<FirefoxOptions> getEmployedOptions() {
        return this.employedOptions
    }

    Optional<String> getEmployedOptionsAsJSON() {
        String json = ""
        this.getEmployedOptions().ifPresent(
                { FirefoxOptions options ->
                    json = JsonOutput.prettyPrint(JsonOutput.toJson(options.toJson()))
                })
        return Optional.of(json)
    }

}




