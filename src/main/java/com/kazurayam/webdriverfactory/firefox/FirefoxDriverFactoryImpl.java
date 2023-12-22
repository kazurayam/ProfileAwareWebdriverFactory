package com.kazurayam.webdriverfactory.firefox;

import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;
import com.kazurayam.webdriverfactory.WebDriverFactoryException;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class FirefoxDriverFactoryImpl extends FirefoxDriverFactory {

    public static Logger getLogger_() {
        return logger_;
    }

    public static void setLogger_(Logger logger_) {
        FirefoxDriverFactoryImpl.logger_ = logger_;
    }

    private static Logger logger_ = LoggerFactory.getLogger(FirefoxDriverFactoryImpl.class);
    private final Set<FirefoxPreferencesModifier> firefoxPreferencesModifiers;
    private final Set<FirefoxOptionsModifier> firefoxOptionsModifiers;
    private Integer pageLoadTimeoutSeconds;

    public FirefoxDriverFactoryImpl() throws IOException {
        this(true);
    }

    public FirefoxDriverFactoryImpl(boolean requireDefaultSettings) throws IOException {
        this.firefoxPreferencesModifiers = new HashSet<>();
        this.firefoxOptionsModifiers = new HashSet<>();
        if (requireDefaultSettings) {
            this.prepareDefaultSettings();
        }
        pageLoadTimeoutSeconds = 60;
    }

    private void prepareDefaultSettings() {
        this.addFirefoxPreferencesModifier(FirefoxPreferencesModifiers.downloadWithoutPrompt());
        this.addFirefoxPreferencesModifier(FirefoxPreferencesModifiers.downloadIntoUserHomeDownloadsDirectory());
        this.addFirefoxOptionsModifier(FirefoxOptionsModifiers.windowSize1024_768());
    }

    @Override
    public FirefoxDriverFactory addFirefoxPreferencesModifier(FirefoxPreferencesModifier fpm) {
        if (this.firefoxPreferencesModifiers.contains(fpm)) {
            // The late comer wins
            this.firefoxPreferencesModifiers.remove(fpm);
        }
        firefoxPreferencesModifiers.add(fpm);
        return this;
    }

    @Override
    public FirefoxDriverFactory addAllFirefoxPreferencesModifier(List<FirefoxPreferencesModifier> list) {
        list.forEach(this::addFirefoxPreferencesModifier);
        return this;
    }

    @Override
    public FirefoxDriverFactory addFirefoxOptionsModifier(FirefoxOptionsModifier fom) {
        if (this.firefoxOptionsModifiers.contains(fom)) {
            // The late comer wins
            this.firefoxOptionsModifiers.remove(fom);
        }
        firefoxOptionsModifiers.add(fom);
        return this;
    }

    @Override
    public FirefoxDriverFactory addAllFirefoxOptionsModifier(List<FirefoxOptionsModifier> list) {
        list.forEach(this::addFirefoxOptionsModifier);
        return this;
    }

    @Override
    public FirefoxDriverFactory pageLoadTimeout(final Integer waitSeconds) {
        Objects.requireNonNull(waitSeconds);
        if (waitSeconds <= 0) {
            throw new IllegalArgumentException("waitSeconds=" + waitSeconds + " must not be <=0");
        }

        if (waitSeconds > 999) {
            throw new IllegalArgumentException("waitSeconds=" + waitSeconds + " must not be > 999");
        }
        this.pageLoadTimeoutSeconds = waitSeconds;
        return this;
    }

    protected static void setPageLoadTimeout(FirefoxDriver driver, Integer seconds) {
        if (seconds != Integer.MIN_VALUE) {
            Duration dur = Duration.ofSeconds((long) seconds);
            long millis = dur.toMillis();
            driver.manage().timeouts().pageLoadTimeout(millis, TimeUnit.MILLISECONDS);
        }

    }

    @Override
    public LaunchedFirefoxDriver newFirefoxDriver() {
        FirefoxOptions options = buildOptions(this.firefoxPreferencesModifiers, this.firefoxOptionsModifiers);
        FirefoxDriver driver = new FirefoxDriver(options);
        setPageLoadTimeout(driver, this.pageLoadTimeoutSeconds);
        LaunchedFirefoxDriver launched = new LaunchedFirefoxDriver(driver).setEmployedOptions(options);
        return launched;
    }

    @Override
    public LaunchedFirefoxDriver newFirefoxDriver(final UserProfile userProfile)
            throws IOException, WebDriverFactoryException
    {
        Objects.requireNonNull(userProfile, "userProfile must not be null");
        Optional<FirefoxUserProfile> opt = FirefoxProfileUtils.findFirefoxUserProfileOf(userProfile);
        assert opt.isPresent();
        FirefoxUserProfile firefoxUserProfile = opt.get();
        ProfileDirectoryName profileDirectoryName = firefoxUserProfile.getProfileDirectoryName();
        Path userDataDir = FirefoxProfileUtils.getDefaultUserDataDir();
        return launchFirefox(userDataDir, profileDirectoryName);
    }

    @Override
    public LaunchedFirefoxDriver newFirefoxDriver(ProfileDirectoryName profileDirectoryName)
            throws WebDriverFactoryException
    {
        Objects.requireNonNull(profileDirectoryName, "profileDirectoryName must not be null");
        Path userDataDir = FirefoxProfileUtils.getDefaultUserDataDir();
        return launchFirefox(userDataDir, profileDirectoryName);
    }

    @Override
    public void enableFirefoxDriverLog(Path outputDirectory) throws IOException {
        Objects.requireNonNull(outputDirectory);
        if (!Files.exists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        }

        FirefoxDriverUtils.enableFirefoxDriverLog(outputDirectory);
    }

    /**
     * Launch a Firefox browser.
     */
    private LaunchedFirefoxDriver launchFirefox(final Path userDataDir, final ProfileDirectoryName profileDirectoryName)
            throws WebDriverFactoryException
    {
        Objects.requireNonNull(userDataDir);
        Objects.requireNonNull(profileDirectoryName);
        if (!Files.exists(userDataDir)) {
            throw new IllegalArgumentException(String.valueOf(userDataDir) + " is not present");
        }

        Path sourceProfileDirectory = userDataDir.resolve(profileDirectoryName.toString());
        assert Files.exists(sourceProfileDirectory);
        final Path targetUserDataDir = userDataDir;

        // use the specified ChromeUserProfile to launch Firefox browser
        this.addFirefoxOptionsModifier(FirefoxOptionsModifiers.withProfileDirectoryName(userDataDir, profileDirectoryName));

        // launch the Firefox driver
        FirefoxDriver driver = null;
        try {
            FirefoxOptions options = buildOptions(this.firefoxPreferencesModifiers, this.firefoxOptionsModifiers);
            driver = new FirefoxDriver(options);
            setPageLoadTimeout(driver, this.pageLoadTimeoutSeconds);
            LaunchedFirefoxDriver launched = new LaunchedFirefoxDriver(driver).setFirefoxUserProfile(new FirefoxUserProfile(targetUserDataDir, profileDirectoryName)).setEmployedOptions(options);
            return launched;
        } catch (InvalidArgumentException iae) {
            if (driver != null) {
                driver.quit();
                logger_.info("forcibly closed the browser");
            }

            StringBuilder sb = new StringBuilder();
            sb.append("targetUserDataDir=\"" + String.valueOf(targetUserDataDir) + "\"\n");
            sb.append("profileDirectoryName=\"" + String.valueOf(profileDirectoryName) + "\"\n");
            sb.append("org.openqa.selenium.InvalidArgumentException was thrown.\n");
            sb.append("Exception message:\n\n");
            sb.append(iae.getMessage());
            throw new WebDriverFactoryException(sb.toString());
        }

    }

    private static FirefoxOptions buildOptions(
            Set<FirefoxPreferencesModifier> firefoxPreferencesModifiers,
            Set<FirefoxOptionsModifier> firefoxOptionsModifiers)
    {
        Map<String, Object> preferences = new HashMap<>();

        preferences = applyFirefoxPreferencesModifiers(preferences, firefoxPreferencesModifiers);

        FirefoxOptions firefoxOptions = FirefoxOptionsBuilder.newInstance(preferences).build();

        firefoxOptions = applyFirefoxOptionsModifiers(firefoxOptions, firefoxOptionsModifiers);

        return firefoxOptions;
    }

    public static Map<String, Object> applyFirefoxPreferencesModifiers(
            Map<String, Object> firefoxPreferences,
            Set<FirefoxPreferencesModifier> modifiers) {
        Map<String, Object> fp = new HashMap<>(firefoxPreferences);
        for (FirefoxPreferencesModifier fpm : modifiers) {
            fp = fpm.modify(fp);
        }
        return fp;
    }

    public static FirefoxOptions applyFirefoxOptionsModifiers(FirefoxOptions firefoxOptions, Set<FirefoxOptionsModifier> modifiers) {
        FirefoxOptions fp = new FirefoxOptions(firefoxOptions);
        for (FirefoxOptionsModifier fom : modifiers) {
            fp = fom.modify(fp);
        }

        return fp;
    }


}
