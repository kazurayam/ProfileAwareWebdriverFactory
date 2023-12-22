package com.kazurayam.webdriverfactory.edge;

import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import com.kazurayam.webdriverfactory.UserDataAccess;
import com.kazurayam.webdriverfactory.UserProfile;
import com.kazurayam.webdriverfactory.WebDriverFactoryException;
import com.kazurayam.webdriverfactory.utils.PathUtils;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class EdgeDriverFactoryImpl extends EdgeDriverFactory {

    private static Logger logger = LoggerFactory.getLogger(EdgeDriverFactoryImpl.class);
    private final Set<EdgePreferencesModifier> edgePreferencesModifiers;
    private final Set<EdgeOptionsModifier> edgeOptionsModifiers;
    private Integer pageLoadTimeoutSeconds;

    public EdgeDriverFactoryImpl() throws IOException {
        this(true);
    }

    public EdgeDriverFactoryImpl(boolean requireDefaultSettings) throws IOException {
        this.edgePreferencesModifiers = new HashSet<>();
        this.edgeOptionsModifiers = new HashSet<>();
        if (requireDefaultSettings) {
            this.prepareDefaultSettings();
        }
        this.pageLoadTimeoutSeconds = 60;
    }

    private void prepareDefaultSettings() {
        this.addEdgePreferencesModifier(EdgePreferencesModifiers.downloadWithoutPrompt());
        this.addEdgePreferencesModifier(EdgePreferencesModifiers.disableViewersOfFlashAndPdf());
        //
        this.addEdgeOptionsModifier(EdgeOptionsModifiers.windowSize1024_768());
        this.addEdgeOptionsModifier(EdgeOptionsModifiers.noSandbox());
        //this.addEdgeOptionsModifier(EdgeOptionsModifiers.singleProcess())
        this.addEdgeOptionsModifier(EdgeOptionsModifiers.disableInfobars());
        this.addEdgeOptionsModifier(EdgeOptionsModifiers.disableExtensions());
        this.addEdgeOptionsModifier(EdgeOptionsModifiers.disableGpu());
        this.addEdgeOptionsModifier(EdgeOptionsModifiers.disableDevShmUsage());
    }

    @Override
    public EdgeDriverFactory addEdgePreferencesModifier(EdgePreferencesModifier epm) {
        if (this.edgePreferencesModifiers.contains(epm)) {
            // The late comer wins
            this.edgePreferencesModifiers.remove(epm);
        }
        this.edgePreferencesModifiers.add(epm);
        return this;
    }

    @Override
    public EdgeDriverFactory addEdgePreferencesModifiers(List<EdgePreferencesModifier> list) {
        list.forEach(this::addEdgePreferencesModifier);
        return this;
    }

    @Override
    public EdgeDriverFactory addEdgeOptionsModifier(EdgeOptionsModifier eom) {
        if (this.edgeOptionsModifiers.contains(eom)) {
            // The late comer wins
            this.edgeOptionsModifiers.remove(eom);
        }
        this.edgeOptionsModifiers.add(eom);
        return this;
    }

    @Override
    public EdgeDriverFactory addEdgeOptionsModifiers(List<EdgeOptionsModifier> list) {
        list.forEach(this::addEdgeOptionsModifier);
        return this;
    }

    @Override
    public EdgeDriverFactory pageLoadTimeout(final Integer waitSeconds) {
        Objects.requireNonNull(waitSeconds);
        if (waitSeconds <= 0) {
            throw new IllegalArgumentException(
                    String.format("waitSeconds=%d must not be <= 0", waitSeconds));
        }
        if (waitSeconds > 999) {
            throw new IllegalArgumentException(
                    String.format("waitSeconds=%d must not be > 999", waitSeconds));
        }
        this.pageLoadTimeoutSeconds = waitSeconds;
        return this;
    }

    protected static void setPageLoadTimeout(EdgeDriver driver, Integer seconds) {
        if (seconds != Integer.MIN_VALUE) {
            Duration dur = Duration.ofSeconds((long) seconds);
            long millis = dur.toMillis();
            driver.manage().timeouts().pageLoadTimeout(millis, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public LaunchedEdgeDriver newEdgeDriver() {
        EdgeOptions options = buildOptions(
                this.edgePreferencesModifiers,
                this.edgeOptionsModifiers);
        EdgeDriver driver = new EdgeDriver(options);
        setPageLoadTimeout(driver, this.pageLoadTimeoutSeconds);
        return new LaunchedEdgeDriver(driver).setEmployedOptions(options);
    }

    @Override
    public LaunchedEdgeDriver newEdgeDriver(UserProfile userProfile) throws IOException, WebDriverFactoryException {
        return newEdgeDriver(userProfile, UserDataAccess.TO_GO);
    }

    @Override
    public LaunchedEdgeDriver newEdgeDriver(final UserProfile userProfile, UserDataAccess instruction) throws IOException, WebDriverFactoryException {
        Objects.requireNonNull(userProfile, "userProfile must not be null");
        Objects.requireNonNull(instruction, "instruction must not be null");
        EdgeUserProfile edgeUserProfile = EdgeUserProfileUtils.findEdgeUserProfile(userProfile);
        if (userProfile == null) {
            throw new WebDriverFactoryException(
                    String.format(
                            "EdgeUserProfile of \"%s\" is not found in :\n%s",
                            userProfile,
                            EdgeUserProfileUtils.allEdgeUserProfilesAsString()
                    )
            );
        }

        Path userDataDir = EdgeUserProfileUtils.getDefaultUserDataDir();
        ProfileDirectoryName profileDirectoryName = edgeUserProfile.getProfileDirectoryName();
        return launchEdge(userDataDir, profileDirectoryName, instruction);
    }

    @Override
    public LaunchedEdgeDriver newEdgeDriver(ProfileDirectoryName profileDirectoryName)
            throws IOException, WebDriverFactoryException {
        return this.newEdgeDriver(profileDirectoryName, UserDataAccess.TO_GO);
    }

    @Override
    public LaunchedEdgeDriver newEdgeDriver(ProfileDirectoryName profileDirectoryName, UserDataAccess instruction)
            throws IOException, WebDriverFactoryException {
        Objects.requireNonNull(profileDirectoryName, "profileDirectoryName must not be null");
        Objects.requireNonNull(instruction, "instruction must not be null");
        Path userDataDir = EdgeUserProfileUtils.getDefaultUserDataDir();
        return launchEdge(userDataDir, profileDirectoryName, instruction);
    }

    @Override
    public void enableEdgeDriverLog(Path outputDirectory) throws IOException {
        Objects.requireNonNull(outputDirectory);
        if (!Files.exists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        }
        EdgeDriverLogConfig.enableChromeDriverLog(outputDirectory);
    }

    private LaunchedEdgeDriver launchEdge(final Path userDataDir, final ProfileDirectoryName profileDirectoryName, UserDataAccess instruction) throws IOException, WebDriverFactoryException {
        Objects.requireNonNull(userDataDir);
        Objects.requireNonNull(profileDirectoryName);
        Objects.requireNonNull(instruction);
        if (!Files.exists(userDataDir)) {
            throw new IllegalArgumentException(String.format("%s is not present", userDataDir));
        }

        final Path sourceProfileDirectory = userDataDir.resolve(profileDirectoryName.toString());
        assert Files.exists(sourceProfileDirectory);
        Path targetUserDataDir = userDataDir;
        if (instruction.equals(UserDataAccess.TO_GO)) {
            targetUserDataDir = Files.createTempDirectory("__user-data-dir__");
            final Path targetProfileDirectory = targetUserDataDir.resolve(profileDirectoryName.getName());
            PathUtils.copyDirectoryRecursively(sourceProfileDirectory, targetProfileDirectory);
            logger.info(String.format("copied %d files from %s into %s",
                    PathUtils.listDirectoryRecursively(targetProfileDirectory).size(),
                    sourceProfileDirectory, targetProfileDirectory));
        } else {
            logger.debug(String.format("%s will be used", sourceProfileDirectory));
        }

        // use the specified ChromeUserProfile with which Chrome browser is launched
        this.addEdgeOptionsModifier(
                EdgeOptionsModifiers.withProfileDirectoryName(
                        targetUserDataDir, profileDirectoryName)
        );

        // launch the Chrome driver
        EdgeDriver driver = null;
        try {
            EdgeOptions options = buildOptions(this.edgePreferencesModifiers, this.edgeOptionsModifiers);
            driver = new EdgeDriver(options);
            setPageLoadTimeout(driver, this.pageLoadTimeoutSeconds);
            return new LaunchedEdgeDriver(driver)
                    .setChromeUserProfile(new EdgeUserProfile(targetUserDataDir, profileDirectoryName))
                    .setInstruction(instruction).setEmployedOptions(options);
        } catch (InvalidArgumentException iae) {
            if (driver != null) {
                driver.quit();
                logger.info("forcibly closed the browser");
            }
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("targetUserDataDir=\"%s\"\n", targetUserDataDir));
            sb.append(String.format("profileDirectoryName=\"%s\"\n", profileDirectoryName));
            sb.append("org.openqa.selenium.InvalidArgumentException was thrown.\n");
            sb.append("Exception message:\n\n");
            sb.append(iae.getMessage());
            throw new WebDriverFactoryException(sb.toString());
        }
    }

    private EdgeOptions buildOptions(Set<EdgePreferencesModifier> edgePreferencesModifiers,
                                       Set<EdgeOptionsModifier> edgeOptionsModifiers) {
        // create a Chrome Preferences object as the seed
        Map<String, Object> preferences = new HashMap<>();

        // modify the instance of Chrome Preferences
        preferences = applyEdgePreferencesModifiers(preferences, edgePreferencesModifiers);

        // create Chrome Options taking over the Chrome Preferences
        EdgeOptions edgeOptions = EdgeOptionsBuilder.newInstance(preferences).build();

        // modify the Chrome Options
        edgeOptions = applyEdgeOptionsModifiers(edgeOptions, edgeOptionsModifiers);

        return edgeOptions;
    }

    public static Map<String, Object> applyEdgePreferencesModifiers(
            Map<String, Object> chromePreferences, Set<EdgePreferencesModifier> modifiers) {
        Map<String, Object> cp = chromePreferences;
        for (EdgePreferencesModifier cpm : modifiers) {
            cp = cpm.modify(cp);
        }
        return cp;
    }

    public static EdgeOptions applyEdgeOptionsModifiers(
            EdgeOptions chromeOptions,
            Set<EdgeOptionsModifier> modifiers) {
        EdgeOptions cp = chromeOptions;
        for (EdgeOptionsModifier com : modifiers) {
            cp = com.modify(cp);
        }
        return cp;
    }


}
