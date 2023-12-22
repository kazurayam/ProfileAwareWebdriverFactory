package com.kazurayam.webdriverfactory.edge;

import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;
import com.kazurayam.webdriverfactory.WebDriverFactoryException;
import com.kazurayam.webdriverfactory.edge.EdgeOptionsModifier;
import com.kazurayam.webdriverfactory.edge.EdgeOptionsModifiers;
import com.kazurayam.webdriverfactory.edge.EdgePreferencesModifier;
import com.kazurayam.webdriverfactory.edge.LaunchedEdgeDriver;
import com.kazurayam.webdriverfactory.UserDataAccess;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public abstract class EdgeDriverFactory {

    public static EdgeDriverFactory newEdgeDriverFactory() throws IOException {
        return new EdgeDriverFactoryImpl();
    }

    public static EdgeDriverFactory newEdgeDriverFactory(boolean requireDefaultSettings)
            throws IOException {
        return new EdgeDriverFactoryImpl(requireDefaultSettings);
    }

    public static EdgeDriverFactory newHeadlessEdgeDriverFactory() throws IOException {
        EdgeDriverFactoryImpl edfl = new EdgeDriverFactoryImpl();
        edfl.addEdgeOptionsModifier(EdgeOptionsModifiers.headless());
        return edfl;
    }

    public static EdgeDriverFactory newHeadlessEdgeDriverFactory(boolean requireDefaultSettings) throws IOException {
        EdgeDriverFactoryImpl edfl = new EdgeDriverFactoryImpl(requireDefaultSettings);
        edfl.addEdgeOptionsModifier(EdgeOptionsModifiers.headless());
        return edfl;
    }

    public abstract EdgeDriverFactory addEdgePreferencesModifier(EdgePreferencesModifier chromePreferencesModifier);

    public abstract EdgeDriverFactory addEdgePreferencesModifiers(List<EdgePreferencesModifier> chromePreferencesModifierList);

    public abstract EdgeDriverFactory addEdgeOptionsModifier(EdgeOptionsModifier chromeOptionsModifier);

    public abstract EdgeDriverFactory addEdgeOptionsModifiers(List<EdgeOptionsModifier> chromeOptionsModifierList);

    public abstract EdgeDriverFactory pageLoadTimeout(Integer waitSeconds);

    public abstract LaunchedEdgeDriver newEdgeDriver();

    public abstract LaunchedEdgeDriver newEdgeDriver(UserProfile userProfile) throws IOException, WebDriverFactoryException;

    public abstract LaunchedEdgeDriver newEdgeDriver(UserProfile userProfile, UserDataAccess instruction) throws IOException, WebDriverFactoryException;

    public abstract LaunchedEdgeDriver newEdgeDriver(ProfileDirectoryName profileDirectoryName) throws IOException, WebDriverFactoryException;

    public abstract LaunchedEdgeDriver newEdgeDriver(ProfileDirectoryName profileDirectoryName, UserDataAccess instruction) throws IOException, WebDriverFactoryException;

    public abstract void enableEdgeDriverLog(Path outputDirectory) throws IOException;

    public static void setPathToEdgeDriverExecutable(String edgeDriverPath) {
        Objects.requireNonNull(edgeDriverPath);
        System.setProperty("webdriver.edge.driver", edgeDriverPath);
    }
}

