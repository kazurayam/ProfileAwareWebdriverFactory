package com.kazurayam.webdriverfactory.chrome

import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities

import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesModifier

abstract class ChromeDriverFactory {

	static ChromeDriverFactory newInstance() {
		return new ChromeDriverFactoryImpl()
	}

	abstract void addChromePreferencesModifier(ChromePreferencesModifier chromePreferencesModifier)

	abstract void addChromeOptionsModifier(ChromeOptionsModifier chromeOptionsModifier)

	abstract void addDesiredCapabilitiesModifier(DesiredCapabilitiesModifier desiredCapabilitiesModifier)

	abstract WebDriver newChromeDriver()

	abstract WebDriver newChromeDriverWithProfile(String userName)

	abstract WebDriver newChromeDriverWithProfileDirectory(String directoryName)

	abstract DesiredCapabilities getEmployedDesiredCapabilities()
}
