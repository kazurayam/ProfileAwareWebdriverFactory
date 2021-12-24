package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.WebDriverFactoryException
import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesModifier
import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesBuilderImpl
import org.apache.commons.io.FileUtils

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

	static {
		// dynamically add toJsonText() method to the built-in classes
		DesiredCapabilities.metaClass.toString = {
			return JsonOutput.prettyPrint(JsonOutput.toJson(delegate.asMap()))
		}
	}

	private final List<ChromePreferencesModifier> chromePreferencesModifiers_
	private final List<ChromeOptionsModifier> chromeOptionsModifiers_
	private final List<DesiredCapabilitiesModifier> desiredCapabilitiesModifiers_

	private DesiredCapabilities desiredCapabilities_

	ChromeDriverFactoryImpl() {
		chromePreferencesModifiers_   = new ArrayList<>()
		chromeOptionsModifiers_       = new ArrayList<>()
		desiredCapabilitiesModifiers_ = new ArrayList<>()
		desiredCapabilities_ = null
	}

	@Override
	void addChromePreferencesModifier(ChromePreferencesModifier chromePreferencesModifier) {
		chromePreferencesModifiers_.add(chromePreferencesModifier)
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
	 * 1. enable logging by Chrome Driver into the tmp directory under the Katalon Studio Project directory
	 * 2. ensure the path of Chrome Driver executable
	 */
	private void prepare() {
		ChromeDriverUtils.enableChromeDriverLog(Paths.get(".").resolve('tmp'))

		this.addChromePreferencesModifier(new ChromePreferencesModifierDefault())
		this.addChromeOptionsModifier(new ChromeOptionsModifierDefault())
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
		Map<String, Object> chromePreferences = new ChromePreferencesBuilderImpl().build()

		// modify the instance of Chrome Preferences
		for (ChromePreferencesModifier cpm in chromePreferencesModifiers_) {
			chromePreferences = cpm.modify(chromePreferences)
		}

		// create Chrome Options taking over the Chrome Preferences
		ChromeOptions chromeOptions = new ChromeOptionsBuilderImpl().build(chromePreferences)
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
	WebDriver newChromeDriverWithProfile(String profileName) {
		Objects.requireNonNull(profileName, "profileName must not be null")
		//
		this.prepare()
		//
		Path profileDirectory = ChromeDriverUtils.getChromeProfileDirectory(profileName)
		if (profileDirectory != null) {
			if (Files.exists(profileDirectory) && profileDirectory.toFile().canWrite()) {

				// copy the Profile directory contents from the Chrome's internal "User Data" directory to temporary directory
				// this is done in order to workaround "User Data is used" contention problem.
				Path userDataDirectory = ChromeDriverUtils.getChromeUserDataDirectory()
				Path tempUDataDirectory = Files.createTempDirectory("User Data")
				Path tempProfileDirectory = tempUDataDirectory.resolve(profileName)
				FileUtils.copyDirectory(profileDirectory.toFile(), tempProfileDirectory.toFile())

				// create the basic ChromeOptionsModifier with copied profile dir
				ChromeOptionsModifier com = new ChromeOptionsModifierWithProfile(tempUDataDirectory, tempProfileDirectory)
				this.addChromeOptionsModifier(com)

				//
				WebDriver driver = null
				try {
					driver = this.execute()
					return driver
				} catch (org.openqa.selenium.InvalidArgumentException iae) {
					if (driver != null) {
						driver.quit()
						logger_.info("forcibly closed the browser")
					}
					StringBuilder sb = new StringBuilder()
					sb.append("profileName=\"${profileName}\"\n")
					sb.append("profileDirectory=\"${profileDirectory}\"\n")
					sb.append("org.openqa.selenium.InvalidArgumentException was thrown.\n")
					sb.append("Exception message: ")
					sb.append(iae.getMessage())
					throw new WebDriverFactoryException(sb.toString())
				}
			} else {
				throw new WebDriverFactoryException(
						"Profile directory \"${profileDirectory.toString()}\" is not present")
			}
		} else {
			throw new WebDriverFactoryException(
					"Profile directory for userName \"${profileName}\" is not found." +
					"\n" + ChromeProfileFinder.listChromeProfiles())
		}
	}

	/**
	 * Usage:
	 * <PRE>
	 * import com.kazurayam.webdriverfactory4ks.ChromeDriverFactory
	 * import import org.openqa.selenium.WebDriver
	 * import com.kms.katalon.core.webui.driver.DriverFactory
	 * 
	 * ChromeDriverFactory cdFactory = new ChromeDriverFactory()
	 *
	 * // open Chrome browser with the profile stored in the directory 'User Data\Default'
	 * WebDriver driver = cdFactory.openChromeDriverWithProfileDirectory('Default')
	 * DriverFactory.changeWebDriver(driver)
	 * WebUI.navigateToUrl('http://demoaut.katalon.com/')
	 * WebUI.delay(3)
	 * WebUI.closeBrowser()
	 * </PRE>
	 */
	@Override
	WebDriver newChromeDriverWithProfileDirectory(String directoryName) throws IOException {
		Objects.requireNonNull(directoryName, "directoryName must not be null")
		Path userDataDir = ChromeDriverUtils.getChromeUserDataDirectory()
		if (userDataDir != null) {
			if (Files.exists(userDataDir)) {
				Path profileDirectory = userDataDir.resolve(directoryName)
				if (Files.exists(profileDirectory)) {
					ChromeProfile chromeProfile = ChromeProfileFinder.getChromeProfileByDirectoryName(profileDirectory)
					return newChromeDriverWithProfile(chromeProfile.getName())
				}
			} else {
				throw new IOException("${userDataDir} is not found")
			}
		} else {
			throw new IOException("unable to identify the User Data Directory of Chrome browser")
		}
	}

	/**
	 * returns the DesiredCapabilitiy object employed when the factory instantiated ChromeDriver by calling execute().
	 * If you call this before calling execute(), null will be returned.
	 */
	@Override
	DesiredCapabilities getEmployedDesiredCapabilities() {
		return this.desiredCapabilities_
	}

}