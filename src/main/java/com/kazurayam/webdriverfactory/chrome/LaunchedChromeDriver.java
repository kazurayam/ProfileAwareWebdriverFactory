package com.kazurayam.webdriverfactory.chrome;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public class LaunchedChromeDriver {

    Logger logger = LoggerFactory.getLogger(LaunchedChromeDriver.class);

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
        Optional<ChromeOptions> options = getEmployedOptions();
        if (options.isPresent()) {
            ChromeOptions opts = options.get();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.convertValue(opts.asMap(), JsonNode.class);
            try {
                return Optional.of(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("unable to convert a ChromeOptions instance into json", e);
            }
        } else {
            return Optional.empty();
        }
    }

    private final ChromeDriver driver;
    private Optional<ChromeUserProfile> chromeUserProfile;
    private Optional<ChromeDriverFactory.UserDataAccess> instruction;
    private Optional<ChromeOptions> employedOptions;

}
