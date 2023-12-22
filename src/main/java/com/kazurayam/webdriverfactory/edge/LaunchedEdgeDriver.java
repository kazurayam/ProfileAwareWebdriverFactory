package com.kazurayam.webdriverfactory.edge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kazurayam.webdriverfactory.UserDataAccess;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.util.Objects;
import java.util.Optional;

public class LaunchedEdgeDriver {

    public LaunchedEdgeDriver(EdgeDriver driver) {
        Objects.requireNonNull(driver);
        this.driver = driver;
        this.edgeUserProfile = Optional.empty();
        this.instruction = Optional.empty();
        this.employedOptions = Optional.empty();
    }

    public EdgeDriver getDriver() {
        return this.driver;
    }

    public LaunchedEdgeDriver setChromeUserProfile(EdgeUserProfile cup) {
        Objects.requireNonNull(cup);
        this.edgeUserProfile = Optional.of(cup);
        return this;
    }

    public Optional<EdgeUserProfile> getEdgeUserProfile() {
        return this.edgeUserProfile;
    }

    public LaunchedEdgeDriver setInstruction(UserDataAccess instruction) {
        Objects.requireNonNull(instruction);
        this.instruction = Optional.of(instruction);
        return this;
    }

    public Optional<UserDataAccess> getInstruction() {
        return this.instruction;
    }

    public LaunchedEdgeDriver setEmployedOptions(EdgeOptions options) {
        Objects.requireNonNull(options);
        this.employedOptions = Optional.of(options);
        return this;
    }

    public Optional<EdgeOptions> getEmployedOptions() {
        return this.employedOptions;
    }

    public Optional<String> getEmployedOptionsAsJSON() {
        Optional<EdgeOptions> options = getEmployedOptions();
        if (options.isPresent()) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return Optional.of(gson.toJson(options.get()));
        } else {
            return Optional.empty();
        }
    }

    private final EdgeDriver driver;
    private Optional<EdgeUserProfile> edgeUserProfile;
    private Optional<UserDataAccess> instruction;
    private Optional<EdgeOptions> employedOptions;

}
