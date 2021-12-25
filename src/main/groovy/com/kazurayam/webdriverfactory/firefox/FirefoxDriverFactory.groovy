package com.kazurayam.webdriverfactory.firefox

import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities

import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesModifier

abstract class FirefoxDriverFactory {

	static FirefoxDriverFactory newInstance() {
		return new FirefoxDriverFactoryImpl()
	}

	abstract void addFirefoxPreferencesModifier(PreferencesModifier firefoxPreferecesModifier)

	abstract void addFirefoxOptionsModifier(FirefoxOptionsModifier firefoxOptionsModifier)

	abstract void addDesiredCapabilitiesModifier(DesiredCapabilitiesModifier desiredCapabilityModifier)

	abstract WebDriver newFirefoxDriver()

	abstract WebDriver newFirefoxDriverWithProfile(String userName)

	abstract DesiredCapabilities getEmployedDesiredCapabilities()
}
