package com.kazurayam.webdriverfactory.firefox;

import com.kazurayam.webdriverfactory.PreferencesModifier;
import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;
import com.kazurayam.webdriverfactory.WebDriverFactoryException;
import groovy.lang.Closure;
import groovy.lang.Reference;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public FirefoxDriverFactoryImpl() {
        this(true);
    }

    public FirefoxDriverFactoryImpl(boolean requireDefaultSettings) {
        this.firefoxPreferencesModifiers = new HashSet<PreferencesModifier>();
        this.firefoxOptionsModifiers = new HashSet<FirefoxOptionsModifier>();
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
    public void addFirefoxPreferencesModifier(PreferencesModifier fpm) {
        if (this.firefoxPreferencesModifiers.contains(fpm)) {
            // The late comer wins
            this.firefoxPreferencesModifiers.remove(fpm);
        }

        firefoxPreferencesModifiers.add(fpm);
    }

    @Override
    public void addAllFirefoxPreferencesModifier(List<PreferencesModifier> list) {
        DefaultGroovyMethods.each(list, new Closure<Boolean>(this, this) {
            public Boolean doCall(PreferencesModifier fpm) {
                return FirefoxDriverFactoryImpl.this.firefoxPreferencesModifiers.add(fpm);
            }

        });
    }

    @Override
    public void addFirefoxOptionsModifier(FirefoxOptionsModifier fom) {
        if (this.firefoxOptionsModifiers.contains(fom)) {
            // The late comer wins
            this.firefoxOptionsModifiers.remove(fom);
        }

        firefoxOptionsModifiers.add(fom);
    }

    @Override
    public void addAllFirefoxOptionsModifier(List<FirefoxOptionsModifier> list) {
        DefaultGroovyMethods.each(list, new Closure<Boolean>(this, this) {
            public Boolean doCall(FirefoxOptionsModifier fom) {
                return FirefoxDriverFactoryImpl.this.firefoxOptionsModifiers.add(fom);
            }

        });
    }

    @Override
    public void pageLoadTimeout(final Integer waitSeconds) {
        Objects.requireNonNull(waitSeconds);
        if (waitSeconds <= 0) {
            throw new IllegalArgumentException("waitSeconds=" + String.valueOf(waitSeconds) + " must not be <=0");
        }

        if (waitSeconds > 999) {
            throw new IllegalArgumentException("waitSeconds=" + String.valueOf(waitSeconds) + " must not be > 999");
        }

        this.pageLoadTimeoutSeconds = waitSeconds;
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
    public LaunchedFirefoxDriver newFirefoxDriver(final UserProfile userProfile) {
        Objects.requireNonNull(userProfile, "userProfile must not be null");
        Optional<FirefoxUserProfile> opt = FirefoxProfileUtils.findFirefoxUserProfileOf(userProfile);
        assert opt.isPresent();
        final Reference<FirefoxUserProfile> firefoxUserProfile;
        opt.ifPresent(new Closure<FirefoxUserProfile>(this, this) {
            public FirefoxUserProfile doCall(Object it) {
                return setGroovyRef(firefoxUserProfile, it);
            }

        });
        if (firefoxUserProfile.get() == null) {
            throw new WebDriverFactoryException("FirefoxUserProfile of \"" + String.valueOf(userProfile) + "\" is not found in :".plus("\n").plus(FirefoxProfileUtils.allFirefoxUserProfilesAsString()));
        }

        Path userDataDir = FirefoxProfileUtils.getDefaultUserDataDir();
        ProfileDirectoryName profileDirectoryName = firefoxUserProfile.get().getProfileDirectoryName();
        return launchFirefox(userDataDir, profileDirectoryName);
    }

    @Override
    public LaunchedFirefoxDriver newFirefoxDriver(ProfileDirectoryName profileDirectoryName) {
        Objects.requireNonNull(profileDirectoryName, "profileDirectoryName must not be null");
        Objects.requireNonNull(getProperty("instruction"), "instruction must not be null");
        Path userDataDir = FirefoxProfileUtils.getDefaultUserDataDir();
        return launchFirefox(userDataDir, profileDirectoryName);
    }

    @Override
    public void enableFirefoxDriverLog(Path outputDirectory) {
        Objects.requireNonNull(outputDirectory);
        if (!Files.exists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        }

        FirefoxDriverUtils.enableFirefoxDriverLog(outputDirectory);
    }

    /**
     * Launch a Firefox browser.
     */
    private LaunchedFirefoxDriver launchFirefox(final Path userDataDir, final ProfileDirectoryName profileDirectoryName) {
        Objects.requireNonNull(userDataDir);
        Objects.requireNonNull(profileDirectoryName);
        if (!Files.exists(userDataDir)) {
            throw new IllegalArgumentException(String.valueOf(userDataDir) + " is not present");
        }

        Path sourceProfileDirectory = userDataDir.resolve(profileDirectoryName.toString());
        assert Files.exists(sourceProfileDirectory);
        final Path targetUserDataDir = userDataDir;

        // use the specified UserProfile to launch Firefox browser
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

    private static FirefoxOptions buildOptions(Set<PreferencesModifier> firefoxPreferencesModifiers, Set<FirefoxOptionsModifier> firefoxOptionsModifiers) {
        Map<String, Object> preferences = new HashMap<String, Object>();

        preferences = applyFirefoxPreferencesModifiers(preferences, firefoxPreferencesModifiers);

        FirefoxOptions firefoxOptions = FirefoxOptionsBuilder.newInstance(preferences).build();

        firefoxOptions = applyFirefoxOptionsModifiers(firefoxOptions, firefoxOptionsModifiers);

        return firefoxOptions;
    }

    public static Map<String, Object> applyFirefoxPreferencesModifiers(Map<String, Object> firefoxPreferences, Set<PreferencesModifier> modifiers) {
        Map<String, Object> fp = new HashMap<String, Object>(firefoxPreferences);
        for (PreferencesModifier fpm : modifiers) {
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

    public static Logger getLogger_() {
        return logger_;
    }

    public static void setLogger_(Logger logger_) {
        FirefoxDriverFactoryImpl.logger_ = logger_;
    }

    private static Logger logger_ = LoggerFactory.getLogger(FirefoxDriverFactoryImpl.class);
    private final Set<PreferencesModifier> firefoxPreferencesModifiers;
    private final Set<FirefoxOptionsModifier> firefoxOptionsModifiers;
    private Integer pageLoadTimeoutSeconds;

    private static <T> T setGroovyRef(Reference<T> ref, T newValue) {
        ref.set(newValue);
        return newValue;
    }
}
