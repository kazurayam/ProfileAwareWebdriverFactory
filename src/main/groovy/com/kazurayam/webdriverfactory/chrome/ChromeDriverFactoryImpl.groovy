package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.PreferencesModifier
import com.kazurayam.webdriverfactory.ProfileDirectoryName
import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.WebDriverFactoryException

import com.kazurayam.webdriverfactory.utils.PathUtils
import org.openqa.selenium.InvalidArgumentException

import java.nio.file.Files
import java.nio.file.Path

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.time.Duration
import java.util.concurrent.TimeUnit

class ChromeDriverFactoryImpl extends ChromeDriverFactory {

	static Logger logger_ = LoggerFactory.getLogger(ChromeDriverFactoryImpl.class)

	private final Set<PreferencesModifier> chromePreferencesModifiers
	private final Set<ChromeOptionsModifier> chromeOptionsModifiers

	private Integer pageLoadTimeoutSeconds

	ChromeDriverFactoryImpl() {
		this(true)
	}

	ChromeDriverFactoryImpl(boolean requireDefaultSettings) {
		this.chromePreferencesModifiers = new HashSet<>()
		this.chromeOptionsModifiers = new HashSet<>()
		if (requireDefaultSettings) {
			this.prepareDefaultSettings()
		}
		pageLoadTimeoutSeconds = 60   // wait for page load for 60 seconds as default
	}

	private void prepareDefaultSettings() {
		this.addChromePreferencesModifier(ChromePreferencesModifiers.downloadWithoutPrompt())
		this.addChromePreferencesModifier(ChromePreferencesModifiers.downloadIntoUserHomeDownloadsDirectory())
		this.addChromePreferencesModifier(ChromePreferencesModifiers.disableViewersOfFlashAndPdf())
		//
		this.addChromeOptionsModifier(ChromeOptionsModifiers.windowSize1024_768())
		this.addChromeOptionsModifier(ChromeOptionsModifiers.noSandbox())
		//this.addChromeOptionsModifier(ChromeOptionsModifiers.singleProcess())
		this.addChromeOptionsModifier(ChromeOptionsModifiers.disableInfobars())
		this.addChromeOptionsModifier(ChromeOptionsModifiers.disableExtensions())
		this.addChromeOptionsModifier(ChromeOptionsModifiers.disableGpu())
		this.addChromeOptionsModifier(ChromeOptionsModifiers.disableDevShmUsage())
	}

	@Override
	void addChromePreferencesModifier(PreferencesModifier cpm) {
		if (this.chromePreferencesModifiers.contains(cpm)) {
			// The late comer wins
			this.chromePreferencesModifiers.remove(cpm)
		}
		this.chromePreferencesModifiers.add(cpm)
	}

	@Override
	void addAllChromePreferencesModifiers(List<PreferencesModifier> list) {
		list.each ({ PreferencesModifier cpm ->
			this.chromePreferencesModifiers.add(cpm)
		})
	}

	@Override
	void addChromeOptionsModifier(ChromeOptionsModifier com) {
		if (this.chromeOptionsModifiers.contains(com)) {
			// The late comer wins
			this.chromeOptionsModifiers.remove(com)
		}
		this.chromeOptionsModifiers.add(com)
	}

	@Override
	void addAllChromeOptionsModifiers(List<ChromeOptionsModifier> list) {
		list.each({ ChromeOptionsModifier com ->
			this.chromeOptionsModifiers.add(com)
		})

	}

	@Override
	void pageLoadTimeout(Integer waitSeconds) {
		Objects.requireNonNull(waitSeconds)
		if (waitSeconds <= 0) {
			throw new IllegalArgumentException("waitSeconds=${waitSeconds} must not be <=0")
		}
		if (waitSeconds > 999) {
			throw new IllegalArgumentException("waitSeconds=${waitSeconds} must not be > 999")
		}
		this.pageLoadTimeoutSeconds = waitSeconds
	}

	static protected void setPageLoadTimeout(ChromeDriver driver, Integer seconds) {
		if (seconds != Integer.MIN_VALUE) {
			Duration dur = Duration.ofSeconds((long)seconds)
			long millis = dur.toMillis()
			driver.manage().timeouts().pageLoadTimeout(millis, TimeUnit.MILLISECONDS);
		}
	}


	@Override
	LaunchedChromeDriver newChromeDriver() {
		ChromeOptions options = buildOptions(
				this.chromePreferencesModifiers,
				this.chromeOptionsModifiers
		)
		ChromeDriver driver = new ChromeDriver(options)
		setPageLoadTimeout(driver, this.pageLoadTimeoutSeconds)
		LaunchedChromeDriver launched =
				new LaunchedChromeDriver(driver).setEmployedOptions(options)
		return launched
	}

	/**
	 *
	 */
	@Override
	LaunchedChromeDriver newChromeDriver(UserProfile userProfile) {
		return newChromeDriver(userProfile, UserDataAccess.TO_GO)
	}

	/**
	 * create a new instance of ChromeDriver using the UserProfile specified.
	 *
	 * If UserDataAccess.CLONE_TO_TEMP is given, create a temporary directory as
	 * the replacement of "User-data" folder into which the profile directory of
	 * the given userProfile is cloned from the original. Effectively we can open
	 * 2 or more Chrome browsers using the same user profile while no IOException
	 * due to contention to the "User-data" folder. However, please note, that all
	 * cookies etc stored in the temporary directory wll be discarded
	 * when the browser is closed; therefore the cookies won't be retained.
	 *
	 * If UserDataAccess.LOCK_USER_DATA is given, the default "User-data" will be
	 * used. When a Chrome using the same user profile has been opened and are
	 * still present, then the "User-data" folder is locked; hence a contention will
	 * occur.
	 * >invalid argument: user data directory is already in use, please specify a unique value for --user-data-dir argument, or don't use --user-data-dir
	 * In this case, you have to close the elder Chrome browser, and try again.
	 *
	 * @param userProfile e.g. new com.kazurayam.webdriverfactory.UserProfile("Alice")
	 * @param instruction default to UserDataAccess.CLONE_TO_TEMP
	 * @return a ChromeDriver object; Chrome browser will be opened.
	 */
	@Override
	LaunchedChromeDriver newChromeDriver(UserProfile userProfile, UserDataAccess instruction) {
		Objects.requireNonNull(userProfile, "userProfile must not be null")
		Objects.requireNonNull(instruction, "instruction must not be null")
		ChromeUserProfile chromeUserProfile = ChromeProfileUtils.findChromeUserProfile(userProfile)
		if (chromeUserProfile == null) {
			throw new WebDriverFactoryException(
					"ChromeUserProfile of \"${userProfile}\" is not found in :" +
							"\n" + ChromeProfileUtils.allChromeUserProfilesAsString())
		}
		Path userDataDir = ChromeProfileUtils.getDefaultUserDataDir()
		ProfileDirectoryName profileDirectoryName = chromeUserProfile.getProfileDirectoryName()
		return launchChrome(userDataDir, profileDirectoryName, instruction)
	}

	@Override
	LaunchedChromeDriver newChromeDriver(ProfileDirectoryName profileDirectoryName) {
		return this.newChromeDriver(profileDirectoryName, UserDataAccess.TO_GO)
	}

	/**
	 *
	 * @param profileFolder e.g. "Default", "Profile 1", "Profile 2"
	 * @param instruction FOR_HERE or TO_GO
	 * @return
	 */
	@Override
	LaunchedChromeDriver newChromeDriver(ProfileDirectoryName profileDirectoryName, UserDataAccess instruction) {
		Objects.requireNonNull(profileDirectoryName, "profileDirectoryName must not be null")
		Objects.requireNonNull(instruction, "instruction must not be null")
		Path userDataDir = ChromeProfileUtils.getDefaultUserDataDir()
		return launchChrome(userDataDir, profileDirectoryName, instruction)
	}


	@Override
	void enableChromeDriverLog(Path outputDirectory) {
		Objects.requireNonNull(outputDirectory)
		if (!Files.exists(outputDirectory)) {
			Files.createDirectories(outputDirectory)
		}
		ChromeDriverUtils.enableChromeDriverLog(outputDirectory)
	}




	/**
	 * Launch a Chrome browser.
	 * If the instruction is UserDataAccess.FOR_HERE, will launch a Chrome with the profile directory
	 * under the userDataDir with the profileDirectoryName.
	 * Else (the instruction is UserDataAccess.TO_GO), will allocate a temporary directory
	 * under which a directory with the profileDirectoryName is created, and into which
	 * the contents of the genuine profile are copied; then a Chrome is launched with the
	 * profileDirectory
	 *
	 * @param userDataDir
	 * @param profileDirectoryName
	 * @param instruction
	 * @return
	 */
	private LaunchedChromeDriver launchChrome(
			Path userDataDir,
			ProfileDirectoryName profileDirectoryName,
			UserDataAccess instruction
	) {
		Objects.requireNonNull(userDataDir)
		Objects.requireNonNull(profileDirectoryName)
		Objects.requireNonNull(instruction)
		if (! Files.exists(userDataDir)) {
			throw new IllegalArgumentException("${userDataDir} is not present")
		}
		Path sourceProfileDirectory = userDataDir.resolve(profileDirectoryName.toString())
		assert Files.exists(sourceProfileDirectory)
		Path targetUserDataDir = userDataDir
		if (instruction == UserDataAccess.TO_GO) {
			targetUserDataDir = Files.createTempDirectory("__user-data-dir__")
			Path targetProfileDirectory = targetUserDataDir.resolve(profileDirectoryName.getName())
			PathUtils.copyDirectoryRecursively(
					sourceProfileDirectory,
					targetProfileDirectory)
			int numCopied = PathUtils.listDirectoryRecursively(targetProfileDirectory).size()
			logger_.info("copied ${numCopied} files from ${sourceProfileDirectory} into ${targetProfileDirectory} ")
		} else {
			logger_.debug("will use ${sourceProfileDirectory} ")
		}

		// use the specified UserProfile with which Chrome browser is launched
		this.addChromeOptionsModifier(
				ChromeOptionsModifiers.withProfileDirectoryName(
						targetUserDataDir, profileDirectoryName))

		// launch the Chrome driver
		ChromeDriver driver = null
		try {
			ChromeOptions options = buildOptions(
					this.chromePreferencesModifiers,
					this.chromeOptionsModifiers
			)
			driver = new ChromeDriver(options)
			setPageLoadTimeout(driver, this.pageLoadTimeoutSeconds)
			LaunchedChromeDriver launched = new LaunchedChromeDriver(driver)
					.setChromeUserProfile(new ChromeUserProfile(targetUserDataDir, profileDirectoryName))
					.setInstruction(instruction)
					.setEmployedOptions(options)
			return launched
		} catch (InvalidArgumentException iae) {
			if (driver != null) {
				driver.quit()
				logger_.info("forcibly closed the browser")
			}
			StringBuilder sb = new StringBuilder()
			sb.append("targetUserDataDir=\"${targetUserDataDir}\"\n")
			sb.append("profileDirectoryName=\"${profileDirectoryName}\"\n")
			sb.append("org.openqa.selenium.InvalidArgumentException was thrown.\n")
			sb.append("Exception message:\n\n")
			sb.append(iae.getMessage())
			throw new WebDriverFactoryException(sb.toString())
		}
	}

	/**
	 * Create an instance of Chrome Driver with configuration
	 * setup through the chain of
	 *     Chrome Preferences => Chrome Options
	 * while modifying each containers with specified Modifiers
	 */
	private ChromeOptions buildOptions(
			Set<PreferencesModifier> chromePreferencesModifiers,
			Set<ChromeOptionsModifier> chromeOptionsModifiers)
	{
		// create a Chrome Preferences object as the seed
		Map<String, Object> preferences = new HashMap<>()

		// modify the instance of Chrome Preferences
		preferences = applyChromePreferencesModifiers(preferences,
				chromePreferencesModifiers)

		// create Chrome Options taking over the Chrome Preferences
		ChromeOptions chromeOptions =
				ChromeOptionsBuilder.newInstance(preferences).build()
		// modify the Chrome Options
		chromeOptions = applyChromeOptionsModifiers(chromeOptions,
				chromeOptionsModifiers)

		return chromeOptions
	}

	static Map<String, Object> applyChromePreferencesModifiers(
			Map<String, Object> chromePreferences,
			Set<PreferencesModifier> modifiers) {
		Map<String, Object> cp = chromePreferences
		for (PreferencesModifier cpm in modifiers) {
			cp = cpm.modify(cp)
		}
		return cp
	}

	static ChromeOptions applyChromeOptionsModifiers(
			ChromeOptions chromeOptions,
			Set<ChromeOptionsModifier> modifiers) {
		ChromeOptions cp = chromeOptions
		for (ChromeOptionsModifier com in modifiers) {
			cp = com.modify(cp)
		}
		return cp
	}
}