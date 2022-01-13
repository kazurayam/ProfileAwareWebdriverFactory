package com.kazurayam.webdriverfactory.firefox;

import groovy.json.JsonOutput;
import groovy.lang.Closure;
import groovy.lang.Reference;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Objects;
import java.util.Optional;

public class LaunchedFirefoxDriver {
    public LaunchedFirefoxDriver(FirefoxDriver driver) {
        Objects.requireNonNull(driver);
        this.driver = driver;
        this.firefoxUserProfile = Optional.empty();
        this.employedOptions = Optional.empty();
    }

    public FirefoxDriver getDriver() {
        return this.driver;
    }

    public LaunchedFirefoxDriver setFirefoxUserProfile(FirefoxUserProfile fup) {
        Objects.requireNonNull(fup);
        this.firefoxUserProfile = Optional.of(fup);
        return this;
    }

    public Optional<FirefoxUserProfile> getFirefoxUserProfile() {
        return this.firefoxUserProfile;
    }

    public LaunchedFirefoxDriver setEmployedOptions(FirefoxOptions options) {
        Objects.requireNonNull(options);
        this.employedOptions = Optional.of(options);
        return this;
    }

    public Optional<FirefoxOptions> getEmployedOptions() {
        return this.employedOptions;
    }

    public Optional<String> getEmployedOptionsAsJSON() {
        final Reference<String> json = new Reference<String>("");
        this.getEmployedOptions().ifPresent(new Closure<String>(this, this) {
            public String doCall(FirefoxOptions options) {
                return setGroovyRef(json, JsonOutput.prettyPrint(JsonOutput.toJson(options.toJson())));
            }

        });
        return Optional.of(json.get());
    }

    private final FirefoxDriver driver;
    private Optional<FirefoxUserProfile> firefoxUserProfile;
    private Optional<FirefoxOptions> employedOptions;

    private static <T> T setGroovyRef(Reference<T> ref, T newValue) {
        ref.set(newValue);
        return newValue;
    }
}
