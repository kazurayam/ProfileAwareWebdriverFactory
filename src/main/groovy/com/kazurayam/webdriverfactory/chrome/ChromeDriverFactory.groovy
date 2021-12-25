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

	abstract WebDriver newChromeDriver(UserProfile userProfile)

	abstract WebDriver newChromeDriver(UserProfile userProfile, UserDataAccess instruction)

	abstract DesiredCapabilities getEmployedDesiredCapabilities()

	enum UserDataAccess {
		LOCK_USER_DATA,
		CLONE_TO_TEMP
	}
}
