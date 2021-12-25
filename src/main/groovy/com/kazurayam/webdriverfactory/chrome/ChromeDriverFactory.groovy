package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.UserProfile
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

	abstract WebDriver newChromeDriverWithUserProfile(UserProfile userProfile)

	abstract WebDriver newChromeDriverWithProfileDirectoryName(ProfileDirectoryName profileDirectoryName)

	abstract DesiredCapabilities getEmployedDesiredCapabilities()
}
