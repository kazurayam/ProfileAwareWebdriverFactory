package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.WebDriverFactoryException
import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesModifier
import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesModifiers
import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesBuilderImpl
import org.apache.commons.io.FileUtils
import org.openqa.selenium.InvalidArgumentException

import java.nio.file.Files
import java.nio.file.Path

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import groovy.json.JsonOutput

class ChromeDriverFactoryImpl extends ChromeDriverFactory {

	static Logger logger_ = LoggerFactory.getLogger(ChromeDriverFactoryImpl.class)

	private final List<ChromePreferencesModifier> chromePreferencesModifiers
	private final List<ChromeOptionsModifier> chromeOptionsModifiers
	private final List<DesiredCapabilitiesModifier> desiredCapabilitiesModifiers

	private DesiredCapabilities desiredCapabilities

	ChromeDriverFactoryImpl() {
		this(true)
	}

	ChromeDriverFactoryImpl(boolean requireDefaultSettings) {
		chromePreferencesModifiers = new ArrayList<>()
		chromeOptionsModifiers = new ArrayList<>()
		desiredCapabilitiesModifiers = new ArrayList<>()
		desiredCapabilities = new DesiredCapabilities()
		if (requireDefaultSettings) {
			this.prepareDefaultSettings()
		}
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
	void addChromePreferencesModifier(ChromePreferencesModifier preferencesModifier) {
		chromePreferencesModifiers.add(preferencesModifier)
	}

	@Override
	void addAllChromePreferencesModifiers(List<ChromePreferencesModifier> list) {
		chromePreferencesModifiers.addAll(list)
	}

	@Override
	void addChromeOptionsModifier(ChromeOptionsModifier chromeOptionsModifier) {
		chromeOptionsModifiers.add(chromeOptionsModifier)
	}

	@Override
	void addAllChromeOptionsModifiers(List<ChromeOptionsModifier> list) {
		chromeOptionsModifiers.addAll(list)
	}

	@Override
	void addDesiredCapabilitiesModifier(DesiredCapabilitiesModifier desiredCapabilitiesModifier) {
		desiredCapabilitiesModifiers.add(desiredCapabilitiesModifier)
	}

	@Override
	void addAllDesiredCapabilitiesModifiers(List<DesiredCapabilitiesModifier> list) {
		desiredCapabilitiesModifiers.addAll(list)
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
	ChromeDriver newChromeDriver() {
		WebDriver driver = this.execute()
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
		Path originalProfileDirectory = chromeUserProfile.getChromeUserProfileDirectory()
		if (!Files.exists(originalProfileDirectory)) {
			throw new WebDriverFactoryException(
					"Profile directory \"${originalProfileDirectory.toString()}\" does not exist")
		}

		Path userDataDirectory = ChromeProfileUtils.getDefaultUserDataDirectory()
		ProfileDirectoryName profileDirectoryName = chromeUserProfile.getProfileDirectoryName()

		return launchChrome(originalProfileDirectory, userDataDirectory, profileDirectoryName, instruction)
	}

	/**
	 *
	 * @param originalProfileDirectory
	 * @param userDataDirectory
	 * @param profileDirectoryName
	 * @param instruction
	 * @return
	 */
	private ChromeDriver launchChrome(Path originalProfileDirectory,
								   Path userDataDirectory,
								   ProfileDirectoryName profileDirectoryName,
								   UserDataAccess instruction) {
		if (instruction == UserDataAccess.TO_GO) {
			// create a temporary directory with name "User Data", into which
			// copy the Profile directory contents from the Chrome's internal "User Data",
			// this is done in order to workaround "User Data is used" contention problem.
			userDataDirectory = Files.createTempDirectory("User Data")
			Path destinationDirectory = userDataDirectory.resolve(profileDirectoryName.getName())
			FileUtils.copyDirectory(
					originalProfileDirectory.toFile(),
					destinationDirectory.toFile())
			logger_.info("copied ${originalProfileDirectory} into ${destinationDirectory} ")
		} else {
			logger_.debug("will use ${originalProfileDirectory} ")
		}

		// use the specified UserProfile with which Chrome browser is launched
		ChromeOptionsModifier com =
				ChromeOptionsModifiers.withUserProfile(
						userDataDirectory,
						profileDirectoryName.getName())
		this.addChromeOptionsModifier(com)

		// launch the Chrome driver
		WebDriver driver = null
		try {
			driver = this.execute()
			return driver
		} catch (InvalidArgumentException iae) {
			if (driver != null) {
				driver.quit()
				logger_.info("forcibly closed the browser")
			}
			StringBuilder sb = new StringBuilder()
			sb.append("profileDirectory=\"${originalProfileDirectory}\"\n")
			sb.append("org.openqa.selenium.InvalidArgumentException was thrown.\n")
			sb.append("Exception message:\n\n")
			sb.append(iae.getMessage())
			throw new WebDriverFactoryException(sb.toString())
		}
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
		ChromeUserProfile chromeUserProfile =
				ChromeProfileUtils.findChromeUserProfileByProfileDirectoryName(profileDirectoryName)
		if (chromeUserProfile == null) {
			throw new WebDriverFactoryException(
					"ChromeUserProfile of directory \"${profileDirectoryName}\" is not found in :" +
							"\n" + ChromeProfileUtils.allChromeUserProfilesAsString())
		}
		Path originalProfileDirectory = chromeUserProfile.getChromeUserProfileDirectory()
		if (!Files.exists(originalProfileDirectory)) {
			throw new WebDriverFactoryException(
					"Profile directory \"${originalProfileDirectory.toString()}\" does not exist")
		}
		Path userDataDirectory = ChromeProfileUtils.getDefaultUserDataDirectory()
		return launchChrome(originalProfileDirectory, userDataDirectory, profileDirectoryName, instruction)
	}




		/**
	 * Create an instance of Chrome Driver with configuration
	 * setup through the chain of
	 *     Chrome Preferences => Chrome Options => Desired Capabilities
	 * while modifying each containers with specified Modifiers
	 *
	 */
	private WebDriver execute() {

		// create a Chrome Preferences object as the seed
		Map<String, Object> preferences = new HashMap<>()

		// modify the instance of Chrome Preferences
		for (ChromePreferencesModifier cpm in chromePreferencesModifiers) {
			preferences = cpm.modify(preferences)
		}

		// create Chrome Options taking over the Chrome Preferences
		ChromeOptions chromeOptions = ChromeOptionsBuilder.newInstance(preferences).build()
		// modify the Chrome Options
		for (ChromeOptionsModifier com in chromeOptionsModifiers) {
			chromeOptions = com.modify(chromeOptions)
		}

		// create Desired Capabilities taking over settings in the Chrome Options
		desiredCapabilities = new DesiredCapabilitiesBuilderImpl().build(chromeOptions)
		// modify the Desired Capabilities
		for (DesiredCapabilitiesModifier dcm in desiredCapabilitiesModifiers) {
			desiredCapabilities = dcm.modify(desiredCapabilities)
		}

		// now launch the browser
		WebDriver driver = new ChromeDriver(desiredCapabilities)

		// well done
		return driver
	}

	@Override
	void enableChromeDriverLog(Path outputDirectory) {
		Objects.requireNonNull(outputDirectory)
		if (!Files.exists(outputDirectory)) {
			Files.createDirectories(outputDirectory)
		}
		ChromeDriverUtils.enableChromeDriverLog ( outputDirectory)
	}
}