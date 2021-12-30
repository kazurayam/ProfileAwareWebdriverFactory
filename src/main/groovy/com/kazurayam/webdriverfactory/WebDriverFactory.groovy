package com.kazurayam.webdriverfactory

import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory.UserDataAccess
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifier
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers
import com.kazurayam.webdriverfactory.chrome.ChromePreferencesModifier
import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesModifier
import org.openqa.selenium.WebDriver


class WebDriverFactory {

	private DriverTypeName driverTypeName
	private UserProfile userProfile
	private UserDataAccess userDataAccess
	private List<ChromePreferencesModifier> chromePreferencesModifierList
	private List<ChromeOptionsModifier> chromeOptionsModifierList
	private List<DesiredCapabilitiesModifier> desiredCapabilitiesModifierList
	private boolean requireDefaultSettings

	private String employedDesiredCapabilities

	private WebDriverFactory(Builder builder) {
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

	/**
	 *
	 */
	static class Builder {
		private DriverTypeName driverTypeName
		private UserProfile userProfile = UserProfile.NULL
		private UserDataAccess userDataAccess = UserDataAccess.TO_GO
		private List<ChromePreferencesModifier> chromePreferencesModifierList = new ArrayList<>()
		private List<ChromeOptionsModifier> chromeOptionsModifierList = new ArrayList<>()
		private List<DesiredCapabilitiesModifier> desiredCapabilitiesModifierList = new ArrayList<>()
		private Boolean requireDefaultSettings = true
		Builder() {
			this(DriverTypeName.CHROME_DRIVER)
		}
		Builder(DriverTypeName driverTypeName) {
			this.driverTypeName = driverTypeName
		}
		Builder userProfile(String userProfile) {
			this.userProfile = new UserProfile(userProfile)
			return this
		}
		Builder userProfile(UserProfile userProfile) {
			this.userProfile = userProfile
			return this
		}
		Builder userDataAccess(UserDataAccess instruction) {
			this.userDataAccess = instruction
			return this
		}
		Builder addChromePreferencesModifier(ChromePreferencesModifier modifier) {
			this.chromePreferencesModifierList.add(modifier)
			return this
		}
		Builder addChromeOptionsModifier(ChromeOptionsModifier modifier) {
			this.chromeOptionsModifierList.add(modifier)
			return this
		}
		Builder addDesiredCapabilitiesModifier(DesiredCapabilitiesModifier modifier) {
			this.desiredCapabilitiesModifierList.add(modifier)
			return this
		}
		Builder requireDefaultSettings(Boolean requireDefaultSettings) {
			this.requireDefaultSettings = requireDefaultSettings
			return this
		}
		WebDriverFactory build() {
			return new WebDriverFactory(this)
		}
	}
}
