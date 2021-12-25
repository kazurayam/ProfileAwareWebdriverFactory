package com.kazurayam.webdriverfactory.chrome

import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities

import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesModifier

abstract class ChromeDriverFactory {

	static ChromeDriverFactory newInstance() {
		return new ChromeDriverFactoryImpl()
	}

	abstract void addPreferencesModifier(PreferencesModifier chromePreferencesModifier)

	abstract void addChromeOptionsModifier(ChromeOptionsModifier chromeOptionsModifier)

	abstract void addDesiredCapabilitiesModifier(DesiredCapabilitiesModifier desiredCapabilitiesModifier)

	abstract WebDriver newChromeDriver()

	abstract WebDriver newChromeDriverWithUserProfile(String userName)

	abstract WebDriver newChromeDriverWithUserProfileDirectoryName(String directoryName)

	abstract DesiredCapabilities getEmployedDesiredCapabilities()
}
