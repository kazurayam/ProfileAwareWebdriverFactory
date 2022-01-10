package com.kazurayam.webdriverfactory.firefox

import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxOptions

abstract class FirefoxDriverFactory {

	static FirefoxDriverFactory newInstance() {
		return new FirefoxDriverFactoryImpl()
	}

	abstract void addFirefoxPreferencesModifier(FirefoxPreferencesModifier firefoxPreferecesModifier)

	abstract void addFirefoxOptionsModifier(FirefoxOptionsModifier firefoxOptionsModifier)

	abstract WebDriver newFirefoxDriver()

	abstract WebDriver newFirefoxDriverWithProfile(String userName)

	abstract FirefoxOptions getEmployedOptions()
}
