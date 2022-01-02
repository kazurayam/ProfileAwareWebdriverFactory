package com.kazurayam.webdriverfactory

import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory.UserDataAccess
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifier
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers
import com.kazurayam.webdriverfactory.chrome.ChromePreferencesModifier
import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesModifier
import org.openqa.selenium.WebDriver


class WebDriverFactoryImpl implements WebDriverFactory {

	private DriverTypeName driverTypeName
	private UserProfile userProfile
	private UserDataAccess userDataAccess
	private List<ChromePreferencesModifier> chromePreferencesModifierList
	private List<ChromeOptionsModifier> chromeOptionsModifierList
	private List<DesiredCapabilitiesModifier> desiredCapabilitiesModifierList
	private boolean requireDefaultSettings

	private String employedDesiredCapabilities

	private WebDriverFactoryImpl(WebDriverFactory.Builder builder) {
		this.driverTypeName = builder.driverTypeName
		this.userProfile = builder.userProfile
		this.userDataAccess = builder.userDataAccess
		this.chromePreferencesModifierList = builder.chromePreferencesModifierList
		this.chromeOptionsModifierList = builder.chromeOptionsModifierList
		this.desiredCapabilitiesModifierList = builder.desiredCapabilitiesModifierList
		this.requireDefaultSettings = builder.requireDefaultSettings
		this.employedDesiredCapabilities = ""
	}

	WebDriver newWebDriver() {
		if (driverTypeName == DriverTypeName.CHROME_DRIVER ||
				driverTypeName == DriverTypeName.HEADLESS_DRIVER) {
			ChromeDriverFactory cdf = ChromeDriverFactory.newInstance(this.requireDefaultSettings)
			if (driverTypeName == DriverTypeName.HEADLESS_DRIVER) {
				cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())  // make it headless
			}
			cdf.addAllChromePreferencesModifiers(this.chromePreferencesModifierList)
			cdf.addAllChromeOptionsModifiers(this.chromeOptionsModifierList)
			cdf.addAllDesiredCapabilitiesModifiers(this.desiredCapabilitiesModifierList)
			WebDriver driver
			if (this.userProfile == UserProfile.NULL) {
				driver = cdf.newChromeDriver()
			} else {
				driver = cdf.newChromeDriver(this.userProfile)
			}
			this.employedDesiredCapabilities = cdf.getEmployedDesiredCapabilitiesAsJSON()
			return driver
		} else {
			throw new RuntimeException("DriverTypeName ${driverTypeName} is not supported")
		}
	}

	String getEmployedDesiredCapabilities() {
		return this.employedDesiredCapabilities
	}

}
