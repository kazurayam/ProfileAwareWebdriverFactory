package com.kazurayam.webdriverfactory

import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory.UserDataAccess
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifier
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers
import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver

import org.openqa.selenium.WebDriver


class WebDriverFactoryImpl implements WebDriverFactory {

	private DriverTypeName driverTypeName
	private UserProfile userProfile
	private UserDataAccess userDataAccess
	private List<PreferencesModifier> chromePreferencesModifierList
	private List<ChromeOptionsModifier> chromeOptionsModifierList
	private boolean requireDefaultSettings

	private String employedOptions

	private WebDriverFactoryImpl(WebDriverFactory.Builder builder) {
		this.driverTypeName = builder.driverTypeName
		this.userProfile = builder.userProfile
		this.userDataAccess = builder.userDataAccess
		this.chromePreferencesModifierList = builder.chromePreferencesModifierList
		this.chromeOptionsModifierList = builder.chromeOptionsModifierList
		this.requireDefaultSettings = builder.requireDefaultSettings
		this.employedOptions = ""
	}

	WebDriver newWebDriver() {
		if (driverTypeName == DriverTypeName.CHROME_DRIVER ||
				driverTypeName == DriverTypeName.HEADLESS_DRIVER) {
			ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory(this.requireDefaultSettings)
			if (driverTypeName == DriverTypeName.HEADLESS_DRIVER) {
				cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())  // make it headless
			}
			cdf.addAllChromePreferencesModifiers(this.chromePreferencesModifierList)
			cdf.addAllChromeOptionsModifiers(this.chromeOptionsModifierList)
			LaunchedChromeDriver launched
			if (this.userProfile == UserProfile.NULL) {
				launched = cdf.newChromeDriver()
			} else {
				launched = cdf.newChromeDriver(this.userProfile)
			}
			WebDriver driver = launched.getDriver()
			this.employedOptions = launched.getEmployedOptionsAsJSON()
			return driver
		} else {
			throw new RuntimeException("DriverTypeName ${driverTypeName} is not supported")
		}
	}

	@Override
	String getEmployedOptions() {
		return this.employedOptions
	}

}
