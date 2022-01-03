package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.UserProfile
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.DesiredCapabilities

import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesModifier

import java.nio.file.Path

abstract class ChromeDriverFactory {

	static ChromeDriverFactory newChromeDriverFactory() {
		return new ChromeDriverFactoryImpl()
	}

	static ChromeDriverFactory newChromeDriverFactory(boolean requireDefaultSettings) {
		return new ChromeDriverFactoryImpl(requireDefaultSettings)
	}

	static ChromeDriverFactory newHeadlessChromeDriverFactory() {
		ChromeDriverFactoryImpl cdfi = new ChromeDriverFactoryImpl()
		cdfi.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		return cdfi
	}

	static ChromeDriverFactory newHeadlessChromeDriverFactory(boolean requireDefaultSettings) {
		ChromeDriverFactoryImpl cdfi = new ChromeDriverFactoryImpl(requireDefaultSettings)
		cdfi.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		return cdfi
	}

	abstract void addChromePreferencesModifier(
			ChromePreferencesModifier chromePreferencesModifier)

	abstract void addAllChromePreferencesModifiers(
			List<ChromePreferencesModifier> chromePreferencesModifierList)

	abstract void addChromeOptionsModifier(
			ChromeOptionsModifier chromeOptionsModifier)

	abstract void addAllChromeOptionsModifiers(
			List<ChromeOptionsModifier> chromeOptionsModifierList)

	abstract void addDesiredCapabilitiesModifier(
			DesiredCapabilitiesModifier desiredCapabilitiesModifier)

	abstract void addAllDesiredCapabilitiesModifiers(
			List<DesiredCapabilitiesModifier> desiredCapabilitiesModifierList)

	abstract ChromeDriver newChromeDriver()

	abstract ChromeDriver newChromeDriver(UserProfile userProfile)

	abstract ChromeDriver newChromeDriver(UserProfile userProfile, UserDataAccess instruction)

	abstract ChromeDriver newChromeDriver(ProfileDirectoryName profileDirectoryName)

	abstract ChromeDriver newChromeDriver(ProfileDirectoryName profileDirectoryName, UserDataAccess instruction)

	abstract DesiredCapabilities getEmployedDesiredCapabilities()

	abstract String getEmployedDesiredCapabilitiesAsJSON()

	abstract void enableChromeDriverLog(Path outputDirectory)

	enum UserDataAccess {
		FOR_HERE,
		TO_GO
	}

	/**
	 *
	 */
	static void setPathToChromeDriverExecutable(String chromeDriverPath) {
		Objects.requireNonNull(chromeDriverPath)
		System.setProperty("webdriver.chrome.driver", chromeDriverPath)
	}

}
