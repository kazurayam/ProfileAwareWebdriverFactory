package com.kazurayam.webdriverfactory.chrome;

import groovy.json.JsonOutput;
import groovy.lang.Closure;
import groovy.lang.Reference;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Objects;
import java.util.Optional;

public class LaunchedChromeDriver {
    public LaunchedChromeDriver(ChromeDriver driver) {
        Objects.requireNonNull(driver);
        this.driver = driver;
        this.chromeUserProfile = Optional.empty();
        this.instruction = Optional.empty();
        this.employedOptions = Optional.empty();
    }

    public ChromeDriver getDriver() {
        return this.driver;
    }

    public LaunchedChromeDriver setChromeUserProfile(ChromeUserProfile cup) {
        Objects.requireNonNull(cup);
        this.chromeUserProfile = Optional.of(cup);
        return this;
    }

    public Optional<ChromeUserProfile> getChromeUserProfile() {
        return this.chromeUserProfile;
    }

    public LaunchedChromeDriver setInstruction(ChromeDriverFactory.UserDataAccess instruction) {
        Objects.requireNonNull(instruction);
        this.instruction = Optional.of(instruction);
        return this;
    }

    public Optional<ChromeDriverFactory.UserDataAccess> getInstruction() {
        return this.instruction;
    }

    public LaunchedChromeDriver setEmployedOptions(ChromeOptions options) {
        Objects.requireNonNull(options);
        this.employedOptions = Optional.of(options);
        return this;
    }

    public Optional<ChromeOptions> getEmployedOptions() {
        return this.employedOptions;
    }

    public Optional<String> getEmployedOptionsAsJSON() {
        final Reference<String> json = new Reference<String>("");
        this.getEmployedOptions().ifPresent(new Closure<String>(this, this) {
            public String doCall(ChromeOptions options) {
                return setGroovyRef(json, JsonOutput.prettyPrint(JsonOutput.toJson(options.toJson())));
            }

        });
        return Optional.of(json.get());
    }

    private final ChromeDriver driver;
    private Optional<ChromeUserProfile> chromeUserProfile;
    private Optional<ChromeDriverFactory.UserDataAccess> instruction;
    private Optional<ChromeOptions> employedOptions;

    private static <T> T setGroovyRef(Reference<T> ref, T newValue) {
        ref.set(newValue);
        return newValue;
    }
}
