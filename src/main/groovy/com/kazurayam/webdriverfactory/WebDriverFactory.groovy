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

	private WebDriverFactory(Builder builder) {
		this.driverTypeName = builder.driverTypeName
		this.userProfile = builder.userProfile
		this.userDataAccess = builder.userDataAccess
		this.chromePreferencesModifierList = builder.chromePreferencesModifierList
		this.chromeOptionsModifierList = builder.chromeOptionsModifierList
		this.desiredCapabilitiesModifierList = builder.desiredCapabilitiesModifierList
		this.requireDefaultSettings = builder.requireDefaultSettings
	}

	WebDriver newWebDriver() {
		if (this.userProfile == UserProfile.NULL) {
			// when UserProfile is not specified
			switch (driverTypeName) {
				case DriverTypeName.CHROME_DRIVER :		// Google Chrome Browser
					ChromeDriverFactory cdf = ChromeDriverFactory.newInstance(this.requireDefaultSettings)
					cdf.addAllChromePreferencesModifiers(this.chromePreferencesModifierList)
					cdf.addAllChromeOptionsModifiers(this.chromeOptionsModifierList)
					cdf.addAllDesiredCapabilitiesModifiers(this.desiredCapabilitiesModifierList)
					return cdf.newChromeDriver()
					break
				case DriverTypeName.HEADLESS_DRIVER :	// Chrome Headless Browser
					ChromeDriverFactory cdf = ChromeDriverFactory.newInstance(this.requireDefaultSettings)
					//
					cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())  // make it headless
					cdf.addAllChromePreferencesModifiers(this.chromePreferencesModifierList)
					cdf.addAllChromeOptionsModifiers(this.chromeOptionsModifierList)
					cdf.addAllDesiredCapabilitiesModifiers(this.desiredCapabilitiesModifierList)
					return cdf.newChromeDriver()
					break
				default:
					throw new RuntimeException("DriverTypeName ${driverTypeName} is not supported")
			}
		} else {
			// when some UserProfile is specified
			switch (driverTypeName) {
				case DriverTypeName.CHROME_DRIVER :
					ChromeDriverFactory cdf = ChromeDriverFactory.newInstance(this.requireDefaultSettings)
					cdf.addAllChromePreferencesModifiers(this.chromePreferencesModifierList)
					cdf.addAllChromeOptionsModifiers(this.chromeOptionsModifierList)
					cdf.addAllDesiredCapabilitiesModifiers(this.desiredCapabilitiesModifierList)
					return cdf.newChromeDriver(userProfile)
					break
				case DriverTypeName.HEADLESS_DRIVER :
					ChromeDriverFactory cdf = ChromeDriverFactory.newInstance(this.requireDefaultSettings)
					//
					cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())  // make it headless
					cdf.addAllChromePreferencesModifiers(this.chromePreferencesModifierList)
					cdf.addAllChromeOptionsModifiers(this.chromeOptionsModifierList)
					cdf.addAllDesiredCapabilitiesModifiers(this.desiredCapabilitiesModifierList)
					return cdf.newChromeDriver(userProfile)
					break
				default:
					throw new RuntimeException("DriverTypeName ${driverTypeName} is not supported")
			}
		}
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
		private boolean requireDefaultSettings = true
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
		Builder requireDefaultSettings(boolean requireDefaultSettings) {
			this.requireDefaultSettings = requireDefaultSettings
		}
		WebDriverFactory build() {
			return new WebDriverFactory(this)
		}
	}
}
