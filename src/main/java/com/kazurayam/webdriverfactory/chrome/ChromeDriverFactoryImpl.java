package com.kazurayam.webdriverfactory.chrome;

import com.kazurayam.webdriverfactory.CacheDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;
import com.kazurayam.webdriverfactory.WebDriverFactoryException;
import com.kazurayam.webdriverfactory.utils.PathUtils;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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

public class ChromeDriverFactoryImpl extends ChromeDriverFactory {

	private static final Logger logger_ = LoggerFactory.getLogger(ChromeDriverFactoryImpl.class);

	private final Set<ChromePreferencesModifier> chromePreferencesModifiers;
	private final Set<ChromeOptionsModifier> chromeOptionsModifiers;
	private Integer pageLoadTimeoutSeconds;

	public ChromeDriverFactoryImpl() throws IOException {
		this(true);
	}

	public ChromeDriverFactoryImpl(boolean requireDefaultSettings) throws IOException {
		this.chromePreferencesModifiers = new HashSet<>();
		this.chromeOptionsModifiers = new HashSet<>();
		if (requireDefaultSettings) {
			this.prepareDefaultSettings();
		}
		pageLoadTimeoutSeconds = 60;// wait for page load for 60 seconds as default
	}

	private void prepareDefaultSettings() {
		this.addChromePreferencesModifier(ChromePreferencesModifiers.downloadWithoutPrompt());
		this.addChromePreferencesModifier(ChromePreferencesModifiers.disableViewersOfFlashAndPdf());
		//
		this.addChromeOptionsModifier(ChromeOptionsModifiers.windowSize1024_768());
		this.addChromeOptionsModifier(ChromeOptionsModifiers.noSandbox());
		//this.addChromeOptionsModifier(ChromeOptionsModifiers.singleProcess())
		this.addChromeOptionsModifier(ChromeOptionsModifiers.disableInfobars());
		this.addChromeOptionsModifier(ChromeOptionsModifiers.disableExtensions());
		this.addChromeOptionsModifier(ChromeOptionsModifiers.disableGpu());
		this.addChromeOptionsModifier(ChromeOptionsModifiers.disableDevShmUsage());
	}

	@Override
	public ChromeDriverFactory addChromePreferencesModifier(ChromePreferencesModifier cpm) {
        // The late comer wins
        this.chromePreferencesModifiers.remove(cpm);
		this.chromePreferencesModifiers.add(cpm);
		return this;
	}

	@Override
	public ChromeDriverFactory addAllChromePreferencesModifiers(List<ChromePreferencesModifier> list) {
		list.forEach(this::addChromePreferencesModifier);
		return this;
	}

	@Override
	public ChromeDriverFactory addChromeOptionsModifier(ChromeOptionsModifier com) {
        // The late comer should wins;
		// so first remove the old if any, then add the new one
        this.chromeOptionsModifiers.remove(com);
		this.chromeOptionsModifiers.add(com);
		return this;
	}

	@Override
	public ChromeDriverFactory addAllChromeOptionsModifiers(List<ChromeOptionsModifier> list) {
		list.forEach(this::addChromeOptionsModifier);
		return this;
	}

	@Override
	public ChromeDriverFactory pageLoadTimeout(final Integer waitSeconds) {
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

	protected static void setPageLoadTimeout(ChromeDriver driver, Integer seconds) {
		if (seconds != Integer.MIN_VALUE) {
			Duration dur = Duration.ofSeconds((long) seconds);
			long millis = dur.toMillis();
			driver.manage().timeouts().pageLoadTimeout(millis, TimeUnit.MILLISECONDS);
		}

	}

	@Override
	public LaunchedChromeDriver newChromeDriver() {
		ChromeOptions options = buildOptions(
				this.chromePreferencesModifiers,
				this.chromeOptionsModifiers);
		logger_.info("[newChromeDriver] this.chromePreferencesModifiers: " + this.chromePreferencesModifiers);
		logger_.info("[newChromeDriver] this.chromeOptionsModifiers: " + this.chromeOptionsModifiers);
		logger_.info("[newChromeDriver] options: " + options);
		ChromeDriver driver = new ChromeDriver(options);
		setPageLoadTimeout(driver, this.pageLoadTimeoutSeconds);
		return new LaunchedChromeDriver(driver).setEmployedOptions(options);
	}

	/**
	 *
	 */
	@Override
	public LaunchedChromeDriver newChromeDriver(UserProfile userProfile) throws IOException, WebDriverFactoryException {
		return newChromeDriver(userProfile, UserDataAccess.TO_GO);
	}

	/**
	 * create a new instance of ChromeDriver using the UserProfile specified.
	 * <p>
	 * If UserDataAccess.TO_GO is given, create a temporary directory as
	 * the replacement of "User-data" folder into which the profile directory of
	 * the given userProfile is cloned from the original. Effectively we can open
	 * 2 or more Chrome browsers using the same user profile while no IOException
	 * due to contention to the "User-data" folder. However, please note, that all
	 * cookies etc stored in the temporary directory wll be discarded
	 * when the browser is closed; therefore the cookies won't be retained to the
	 * next session.</p>
	 * <p>
	 * If UserDataAccess.FOR_HERE is given, the default "User-data" will be
	 * used. When a Chrome using the same user profile has been opened and are
	 * still present, then the "User-data" folder is locked; hence a contention will
	 * occur.</p>
	 *
	 * <p>invalid argument: user data directory is already in use,
	 * please specify a unique value for --user-data-dir argument,
	 * or don't use --user-data-dir
	 * In this case, you have to close the elder Chrome browser, and try again.</p>
	 *
	 * @param userProfile e.g. new com.kazurayam.webdriverfactory.UserProfile("Alice")
	 * @param instruction default to UserDataAccess.CLONE_TO_TEMP
	 * @return a ChromeDriver object; Chrome browser will be opened.
	 */
	@Override
	public LaunchedChromeDriver newChromeDriver(final UserProfile userProfile, UserDataAccess instruction) throws IOException, WebDriverFactoryException {
		Objects.requireNonNull(userProfile, "userProfile must not be null");
		Objects.requireNonNull(instruction, "instruction must not be null");
		ChromeUserProfile chromeUserProfile = ChromeProfileUtils.findChromeUserProfile(userProfile);
		if (chromeUserProfile == null) {
			throw new WebDriverFactoryException(
					String.format(
							"ChromeUserProfile of \"%s\" is not found in :\n%s",
							userProfile,
							ChromeProfileUtils.allChromeUserProfilesAsString()
					)
			);
		}

		Path userDataDir = ChromeProfileUtils.getDefaultUserDataDir();
		CacheDirectoryName cacheDirectoryName = chromeUserProfile.getCacheDirectoryName();
		return launchChrome(userDataDir, cacheDirectoryName, instruction);
	}

	@Override
	public LaunchedChromeDriver newChromeDriver(CacheDirectoryName cacheDirectoryName)
			throws IOException, WebDriverFactoryException {
		return this.newChromeDriver(cacheDirectoryName, UserDataAccess.TO_GO);
	}

	/**
	 * @param cacheDirectoryName e.g. "Default", "Profile 1", "Profile 2"
	 * @param instruction   FOR_HERE or TO_GO
	 */
	@Override
	public LaunchedChromeDriver newChromeDriver(
			CacheDirectoryName cacheDirectoryName,
			UserDataAccess instruction)
			throws IOException, WebDriverFactoryException {
		Objects.requireNonNull(cacheDirectoryName,
				"cacheDirectoryName must not be null");
		Objects.requireNonNull(instruction,
				"instruction must not be null");
		Path userDataDir = ChromeProfileUtils.getDefaultUserDataDir();
		return launchChrome(userDataDir, cacheDirectoryName, instruction);
	}

	@Override
	public void enableChromeDriverLog(Path outputDirectory) throws IOException {
		Objects.requireNonNull(outputDirectory);
		if (!Files.exists(outputDirectory)) {
			Files.createDirectories(outputDirectory);
		}

		ChromeDriverUtils.enableChromeDriverLog(outputDirectory);
	}

	/**
	 * Launch a Chrome browser.
	 * If the instruction is UserDataAccess.FOR_HERE, will launch a Chrome with the profile directory
	 * under the userDataDir with the cacheDirectoryName.
	 * Else (the instruction is UserDataAccess.TO_GO), will allocate a temporary directory
	 * under which a directory with the cacheDirectoryName is created, and into which
	 * the contents of the genuine profile are copied; then a Chrome is launched with the
	 * profileDirectory
	 *
	 * @param userDataDir
	 * @param cacheDirectoryName
	 * @param instruction
	 * @return
	 */
	private LaunchedChromeDriver launchChrome(final Path userDataDir,
											  final CacheDirectoryName cacheDirectoryName,
											  UserDataAccess instruction)
			throws IOException, WebDriverFactoryException {
		Objects.requireNonNull(userDataDir);
		Objects.requireNonNull(cacheDirectoryName);
		Objects.requireNonNull(instruction);
		if (!Files.exists(userDataDir)) {
			throw new IllegalArgumentException(String.format("%s is not present", userDataDir));
		}

		final Path sourceProfileDirectory = userDataDir.resolve(cacheDirectoryName.toString());
		assert Files.exists(sourceProfileDirectory);
		Path targetUserDataDir = userDataDir;
		if (instruction.equals(UserDataAccess.TO_GO)) {
			targetUserDataDir = Files.createTempDirectory("__user-data-dir__");
			final Path targetProfileDirectory = targetUserDataDir.resolve(cacheDirectoryName.getName());
			PathUtils.copyDirectoryRecursively(sourceProfileDirectory, targetProfileDirectory);
			logger_.info(String.format("copied %d files from %s into %s",
					PathUtils.listDirectoryRecursively(targetProfileDirectory).size(),
					sourceProfileDirectory, targetProfileDirectory));
		} else {
			logger_.debug(String.format("%s will be used", sourceProfileDirectory));
		}

		// use the specified UserProfile with which Chrome browser is launched
		this.addChromeOptionsModifier(
				ChromeOptionsModifiers.withCacheDirectoryName(
						targetUserDataDir, cacheDirectoryName)
		);

		// launch the Chrome driver
		ChromeDriver driver = null;
		try {
			ChromeOptions options = buildOptions(this.chromePreferencesModifiers, this.chromeOptionsModifiers);
			driver = new ChromeDriver(options);
			setPageLoadTimeout(driver, this.pageLoadTimeoutSeconds);
			return new LaunchedChromeDriver(driver)
					.setChromeUserProfile(new ChromeUserProfile(targetUserDataDir, cacheDirectoryName))
					.setInstruction(instruction).setEmployedOptions(options);
		} catch (InvalidArgumentException iae) {
			if (driver != null) {
				driver.quit();
				logger_.info("forcibly closed the browser");
			}
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("targetUserDataDir=\"%s\"\n", targetUserDataDir));
			sb.append(String.format("cacheDirectoryName=\"%s\"\n", cacheDirectoryName));
			sb.append("org.openqa.selenium.InvalidArgumentException was thrown.\n");
			sb.append("Exception message:\n\n");
			sb.append(iae.getMessage());
			throw new WebDriverFactoryException(sb.toString());
		}
	}

	/**
	 * Create an instance of Chrome Driver with configuration
	 * setup through the chain of
	 * Chrome Preferences => Chrome Options
	 * while modifying each containers with specified Modifiers
	 */
	public static ChromeOptions buildOptions(Set<ChromePreferencesModifier> chromePreferencesModifiers,
									   Set<ChromeOptionsModifier> chromeOptionsModifiers) {
		// create a Chrome Preferences object as the seed
		Map<String, Object> preferences = new HashMap<>();

		// modify the instance of Chrome Preferences
		preferences = applyChromePreferencesModifiers(preferences, chromePreferencesModifiers);

		// create Chrome Options taking over the Chrome Preferences
		ChromeOptions chromeOptions = ChromeOptionsBuilder.newInstance(preferences).build();

		// modify the Chrome Options
		chromeOptions = applyChromeOptionsModifiers(chromeOptions, chromeOptionsModifiers);

		return chromeOptions;
	}

	public static Map<String, Object> applyChromePreferencesModifiers(
			Map<String, Object> chromePreferences, Set<ChromePreferencesModifier> modifiers) {
		Map<String, Object> cp = chromePreferences;
		for (ChromePreferencesModifier cpm : modifiers) {
			cp = cpm.modify(cp);
		}
		return cp;
	}

	public static ChromeOptions applyChromeOptionsModifiers(
			ChromeOptions chromeOptions,
			Set<ChromeOptionsModifier> modifiers) {
		ChromeOptions cp = chromeOptions;
		for (ChromeOptionsModifier com : modifiers) {
			cp = com.modify(cp);
		}
		return cp;
	}

}
