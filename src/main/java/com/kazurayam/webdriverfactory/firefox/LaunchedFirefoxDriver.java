package com.kazurayam.webdriverfactory.firefox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        String json = "";
        this.getEmployedOptions().ifPresent(options -> {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(options);
        });
        return Optional.of(json);
    }

    private final FirefoxDriver driver;
    private Optional<FirefoxUserProfile> firefoxUserProfile;
    private Optional<FirefoxOptions> employedOptions;
}
