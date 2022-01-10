package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.WebDriverFactoryException
import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesModifier
import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesModifiers
import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesBuilderImpl
import com.kazurayam.webdriverfactory.utils.PathUtils
import org.openqa.selenium.InvalidArgumentException

import java.nio.file.Files
import java.nio.file.Path

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import groovy.json.JsonOutput

import java.time.Duration
import java.util.concurrent.TimeUnit

class ChromeDriverFactoryImpl extends ChromeDriverFactory {

	static Logger logger_ = LoggerFactory.getLogger(ChromeDriverFactoryImpl.class)

	private final Set<ChromePreferencesModifier> chromePreferencesModifiers
	private final Set<ChromeOptionsModifier> chromeOptionsModifiers
	private final List<DesiredCapabilitiesModifier> desiredCapabilitiesModifiers

	private DesiredCapabilities desiredCapabilities
	private Integer pageLoadTimeoutSeconds

	ChromeDriverFactoryImpl() {
		this(true)
	}

	ChromeDriverFactoryImpl(boolean requireDefaultSettings) {
		this.chromePreferencesModifiers = new HashSet<>()
		this.chromeOptionsModifiers = new HashSet<>()
		this.desiredCapabilitiesModifiers = new ArrayList<>()
		desiredCapabilities = new DesiredCapabilities()
		if (requireDefaultSettings) {
			this.prepareDefaultSettings()
		}
		pageLoadTimeoutSeconds = 30   // perform implicit wait for 30 seconds
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
		//this.addChromeOptionsModifier(ChromeOptionsModifiers.disableExtensions())
		this.addChromeOptionsModifier(ChromeOptionsModifiers.disableGpu())
		this.addChromeOptionsModifier(ChromeOptionsModifiers.disableDevShmUsage())
		//
		this.addDesiredCapabilitiesModifier(DesiredCapabilitiesModifiers.passThrough())
	}

	@Override
	void addChromePreferencesModifier(ChromePreferencesModifier cpm) {
		if (this.chromePreferencesModifiers.contains(cpm)) {
			// The late comer wins
			this.chromePreferencesModifiers.remove(cpm)
		}
		this.chromePreferencesModifiers.add(cpm)
	}

	@Override
	void addAllChromePreferencesModifiers(List<ChromePreferencesModifier> list) {
		list.each ({ ChromePreferencesModifier cpm ->
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
	void addDesiredCapabilitiesModifier(DesiredCapabilitiesModifier desiredCapabilitiesModifier) {
		this.desiredCapabilitiesModifiers.add(desiredCapabilitiesModifier)
	}

	@Override
	void addAllDesiredCapabilitiesModifiers(List<DesiredCapabilitiesModifier> list) {
		list.each ({ DesiredCapabilitiesModifier dcm ->
			this.desiredCapabilitiesModifiers.add(dcm)
		})
	}


	/**
	 * returns the DesiredCapability object employed when the factory instantiated ChromeDriver by calling execute().
	 * If you call this before calling execute(), null will be returned.
	 */
	@Override
	DesiredCapabilities getEmployedDesiredCapabilities() {
		return this.desiredCapabilities
	}

	@Override
	String getEmployedDesiredCapabilitiesAsJSON() {
		// dynamically override the toJSON() method of the Selenium's built-in class
		// so that it print in JSON pretty format
		DesiredCapabilities.metaClass.toJSON = {
			return JsonOutput.prettyPrint(JsonOutput.toJson(delegate.asMap()))
		}
		return this.getEmployedDesiredCapabilities().toJSON()
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
	ChromeDriver newChromeDriver() {
		this.desiredCapabilities = buildDesiredCapabilities(
				this.chromePreferencesModifiers,
				this.chromeOptionsModifiers,
				this.desiredCapabilitiesModifiers
		)
		ChromeDriver driver = new ChromeDriver(this.desiredCapabilities)
		setPageLoadTimeout(driver, this.pageLoadTimeoutSeconds)
		driver.metaClass.userProfile = Optional.empty()
		driver.metaClass.userDataAccess = Optional.empty()
		driver.metaClass.desiredCapabilities = Optional.of (this.employedDesiredCapabilitiesAsJSON)
		return driver
	}

	/**
	 *
	 */
	@Override
	ChromeDriver newChromeDriver(UserProfile userProfile) {
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
	ChromeDriver newChromeDriver(UserProfile userProfile, UserDataAccess instruction) {
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
	ChromeDriver newChromeDriver(ProfileDirectoryName profileDirectoryName) {
		return this.newChromeDriver(profileDirectoryName, UserDataAccess.TO_GO)
	}

	/**
	 *
	 * @param profileFolder e.g. "Default", "Profile 1", "Profile 2"
	 * @param instruction FOR_HERE or TO_GO
	 * @return
	 */
	@Override
	ChromeDriver newChromeDriver(ProfileDirectoryName profileDirectoryName, UserDataAccess instruction) {
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
		ChromeDriverUtils.enableChromeDriverLog ( outputDirectory)
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
	private ChromeDriver launchChrome(Path userDataDir,
									  ProfileDirectoryName profileDirectoryName,
									  UserDataAccess instruction) {
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
		ChromeOptionsModifier com =
				ChromeOptionsModifiers.withUserProfile(
						targetUserDataDir,
						profileDirectoryName.getName())
		this.addChromeOptionsModifier(com)

		// launch the Chrome driver
		ChromeDriver driver = null
		try {
			this.desiredCapabilities = buildDesiredCapabilities(
					this.chromePreferencesModifiers,
					this.chromeOptionsModifiers,
					this.desiredCapabilitiesModifiers
			)
			driver = new ChromeDriver(this.desiredCapabilities)
			setPageLoadTimeout(driver, this.pageLoadTimeoutSeconds)
			//
			ChromeUserProfile cup = new ChromeUserProfile(targetUserDataDir, profileDirectoryName)
			driver.metaClass.userProfile = Optional.of(cup)
			driver.metaClass.userDataAccess = Optional.of(instruction)
			driver.metaClass.desiredCapabilities = Optional.of (this.employedDesiredCapabilitiesAsJSON)
			//
			return driver
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
	 *     Chrome Preferences => Chrome Options => Desired Capabilities
	 * while modifying each containers with specified Modifiers
	 */
	private DesiredCapabilities buildDesiredCapabilities(
			Set<ChromePreferencesModifier> chromePreferencesModifiers,
			Set<ChromeOptionsModifier> chromeOptionsModifiers,
			List<DesiredCapabilitiesModifier> desiredCapabilitiesModifiers
	) {
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

		// create Desired Capabilities taking over settings in the Chrome Options
		DesiredCapabilities desiredCapabilities =
				new DesiredCapabilitiesBuilderImpl().build(chromeOptions)
		// modify the Desired Capabilities
		desiredCapabilities = applyDesiredCapabilitiesModifiers(desiredCapabilities,
				desiredCapabilitiesModifiers)

		return desiredCapabilities
	}

	static Map<String, Object> applyChromePreferencesModifiers(
			Map<String, Object> chromePreferences, Set<ChromePreferencesModifier> modifiers) {
		Map<String, Object> cp = chromePreferences
		for (ChromePreferencesModifier cpm in modifiers) {
			cp = cpm.modify(cp)
		}
		return cp
	}

	static ChromeOptions applyChromeOptionsModifiers(
			ChromeOptions chromeOptions, Set<ChromeOptionsModifier> modifiers) {
		ChromeOptions cp = chromeOptions
		for (ChromeOptionsModifier com in modifiers) {
			cp = com.modify(cp)
		}
		return cp
	}

	static DesiredCapabilities applyDesiredCapabilitiesModifiers(
			DesiredCapabilities desiredCapabilities, List<DesiredCapabilitiesModifier> modifiers) {
		DesiredCapabilities dc = desiredCapabilities
		for (DesiredCapabilitiesModifier dcm in modifiers) {
			dc = dcm.modify(dc)
		}
		return dc
	}

}