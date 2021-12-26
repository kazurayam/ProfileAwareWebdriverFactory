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
import java.nio.file.Paths

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import groovy.json.JsonOutput

class ChromeDriverFactoryImpl extends ChromeDriverFactory {

	static Logger logger_ = LoggerFactory.getLogger(ChromeDriverFactoryImpl.class)

	private final List<PreferencesModifier> preferencesModifiers_
	private final List<ChromeOptionsModifier> chromeOptionsModifiers_
	private final List<DesiredCapabilitiesModifier> desiredCapabilitiesModifiers_

	private DesiredCapabilities desiredCapabilities_

	ChromeDriverFactoryImpl() {
		preferencesModifiers_         = new ArrayList<>()
		chromeOptionsModifiers_       = new ArrayList<>()
		desiredCapabilitiesModifiers_ = new ArrayList<>()
		desiredCapabilities_ = null
	}

	@Override
	void addPreferencesModifier(PreferencesModifier preferencesModifier) {
		preferencesModifiers_.add(preferencesModifier)
	}

	@Override
	void addChromeOptionsModifier(ChromeOptionsModifier chromeOptionsModifier) {
		chromeOptionsModifiers_.add(chromeOptionsModifier)
	}

	@Override
	void addDesiredCapabilitiesModifier(DesiredCapabilitiesModifier desiredCapabilitiesModifier) {
		desiredCapabilitiesModifiers_.add(desiredCapabilitiesModifier)
	}

	/**
	 * returns the DesiredCapability object employed when the factory instantiated ChromeDriver by calling execute().
	 * If you call this before calling execute(), null will be returned.
	 */
	@Override
	DesiredCapabilities getEmployedDesiredCapabilities() {
		return this.desiredCapabilities_
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
	WebDriver newChromeDriver() {
		this.prepare()
		WebDriver driver = this.execute()
		return driver
	}

	/**
	 * Based on the post https://forum.katalon.com/t/open-browser-with-custom-profile/19268 by Thanh To
	 *
	 * Chrome's User Data directory is OS dependent. The User Data Directory is described in the document
	 * https://chromium.googlesource.com/chromium/src/+/HEAD/docs/user_data_dir.md#Current-Location
	 */
	@Override
	WebDriver newChromeDriver(UserProfile userProfile) {
		return newChromeDriver(userProfile, UserDataAccess.CLONE_TO_TEMP)
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
	WebDriver newChromeDriver(UserProfile userProfile, UserDataAccess instruction) {
		Objects.requireNonNull(userProfile, "userProfileName must not be null")
		ChromeUserProfile chromeUserProfile = ChromeProfileUtils.findChromeUserProfile(userProfile)
		if (chromeUserProfile == null) {
			throw new WebDriverFactoryException(
					"ChromeUserProfile of \"${userProfile}\" is not found in :" +
							"\n" + ChromeProfileUtils.allChromeUserProfilesAsString())
		}
		Path originalProfileDirectory = chromeUserProfile.getChromeUserProfileDirectory()
		if (! Files.exists(originalProfileDirectory)) {
			throw new WebDriverFactoryException(
					"Profile directory \"${originalProfileDirectory.toString()}\" does not exist")
		}

		Path userDataDirectory = ChromeProfileUtils.getDefaultUserDataDirectory()
		ProfileDirectoryName profileDirectoryName = chromeUserProfile.getProfileDirectoryName()

		if (instruction == UserDataAccess.CLONE_TO_TEMP) {
			// create a temporary directory with name "User Data", into which
			// copy the Profile directory contents from the Chrome's internal "User Data",
			// this is done in order to workaround "User Data is used" contention problem.
			userDataDirectory = Files.createTempDirectory("User Data")
			Path destinationDirectory = userDataDirectory.resolve(profileDirectoryName.getName())
			FileUtils.copyDirectory(
					originalProfileDirectory.toFile(),
					destinationDirectory.toFile())
			logger_.info("copied ${originalProfileDirectory} into ${destinationDirectory} " +
					"as the profile directory of ${userProfile}")
		} else {
			logger_.debug("will use ${originalProfileDirectory} " +
					"as the profile directory of ${userProfile}")
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
			this.prepare()
			driver = this.execute()
			return driver
		} catch (InvalidArgumentException iae) {
			if (driver != null) {
				driver.quit()
				logger_.info("forcibly closed the browser")
			}
			StringBuilder sb = new StringBuilder()
			sb.append("userProfileName=\"${userProfile}\"\n")
			sb.append("profileDirectory=\"${originalProfileDirectory}\"\n")
			sb.append("org.openqa.selenium.InvalidArgumentException was thrown.\n")
			sb.append("Exception message:\n\n")
			sb.append(iae.getMessage())
			throw new WebDriverFactoryException(sb.toString())
		}
	}

	/**
	 * 1. enable logging by Chrome Driver into the tmp directory under the Katalon Studio Project directory
	 * 2. ensure the path of Chrome Driver executable
	 */
	private void prepare() {
		ChromeDriverUtils.enableChromeDriverLog(Paths.get(".").resolve('tmp'))

		//this.addPreferencesModifier(new PreferencesModifierDefault())
		this.addPreferencesModifier(PreferencesModifiers.downloadWithoutPrompt())
		this.addPreferencesModifier(PreferencesModifiers.downloadIntoUserHomeDownloadsDirectory())
		this.addPreferencesModifier(PreferencesModifiers.disableViewersOfFlashAndPdf())

		//this.addChromeOptionsModifier(new ChromeOptionsModifierDefault())
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
		for (PreferencesModifier cpm in preferencesModifiers_) {
			preferences = cpm.modify(preferences)
		}

		// create Chrome Options taking over the Chrome Preferences
		ChromeOptions chromeOptions = ChromeOptionsBuilder.newInstance(preferences).build()
		// modify the Chrome Options
		for (ChromeOptionsModifier com in chromeOptionsModifiers_) {
			chromeOptions = com.modify(chromeOptions)
		}

		// create Desired Capabilities taking over settings in the Chrome Options
		desiredCapabilities_ = new DesiredCapabilitiesBuilderImpl().build(chromeOptions)
		// modify the Desired Capabilities
		for (DesiredCapabilitiesModifier dcm in desiredCapabilitiesModifiers_) {
			desiredCapabilities_ = dcm.modify(desiredCapabilities_)
		}

		// now launch the browser
		WebDriver driver = new ChromeDriver(desiredCapabilities_)

		// well done
		return driver
	}



}