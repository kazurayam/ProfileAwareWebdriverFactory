package com.kazurayam.webdriverfactory.firefox;

import com.kazurayam.webdriverfactory.PreferencesModifier;
import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;
import com.kazurayam.webdriverfactory.WebDriverFactoryException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public abstract class FirefoxDriverFactory {
    public static FirefoxDriverFactory newFirefoxDriverFactory() {
        return new FirefoxDriverFactoryImpl();
    }

    public static FirefoxDriverFactory newFirefoxDriverFactory(boolean requireDefaultSettings) {
        return new FirefoxDriverFactoryImpl(requireDefaultSettings);
    }

    public static FirefoxDriverFactory newHeadlessFirefoxDriverFactory() {
        FirefoxDriverFactoryImpl fdfi = new FirefoxDriverFactoryImpl();
        fdfi.addFirefoxOptionsModifier(FirefoxOptionsModifiers.headless());
        return fdfi;
    }

    public static FirefoxDriverFactory newHeadlessFirefoxDriverFactory(boolean requireDefaultSettings) {
        FirefoxDriverFactoryImpl fdfi = new FirefoxDriverFactoryImpl(requireDefaultSettings);
        fdfi.addFirefoxOptionsModifier(FirefoxOptionsModifiers.headless());
        return fdfi;
    }

    public abstract void addFirefoxPreferencesModifier(PreferencesModifier firefoxPreferencesModifier);

    public abstract void addAllFirefoxPreferencesModifier(List<PreferencesModifier> firefoxPreferencesModifierList);

    public abstract void addFirefoxOptionsModifier(FirefoxOptionsModifier firefoxOptionsModifier);

    public abstract void addAllFirefoxOptionsModifier(List<FirefoxOptionsModifier> firefoxOptionsModifierList);

    public abstract void pageLoadTimeout(Integer waitSeconds);

    public abstract LaunchedFirefoxDriver newFirefoxDriver();

    public abstract LaunchedFirefoxDriver newFirefoxDriver(UserProfile userProfile) throws IOException, WebDriverFactoryException;

    public abstract LaunchedFirefoxDriver newFirefoxDriver(ProfileDirectoryName profileDirectoryName) throws WebDriverFactoryException;

    public abstract void enableFirefoxDriverLog(Path outputDirectory) throws IOException;

    public static void setPathToFirefoxDriverExecutable(String geckoDriverPath) {
        Objects.requireNonNull(geckoDriverPath);
        System.setProperty("webdriver.gecko.driver", geckoDriverPath);
    }

}
