package com.kazurayam.webdriverfactory

import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory.UserDataAccess
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifier
import org.openqa.selenium.WebDriver


interface WebDriverFactory {

	WebDriver newWebDriver()

	String getEmployedOptions()

	/**
	 *
	 */
	static class Builder {
		protected DriverTypeName driverTypeName
		protected UserProfile userProfile = UserProfile.NULL
		protected UserDataAccess userDataAccess = UserDataAccess.TO_GO
		protected List<PreferencesModifier> chromePreferencesModifierList = new ArrayList<>()
		protected List<ChromeOptionsModifier> chromeOptionsModifierList = new ArrayList<>()
		protected Boolean requireDefaultSettings = true
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
		Builder addChromePreferencesModifier(PreferencesModifier modifier) {
			this.chromePreferencesModifierList.add(modifier)
			return this
		}
		Builder addChromeOptionsModifier(ChromeOptionsModifier modifier) {
			this.chromeOptionsModifierList.add(modifier)
			return this
		}
		Builder requireDefaultSettings(Boolean requireDefaultSettings) {
			this.requireDefaultSettings = requireDefaultSettings
			return this
		}
		WebDriverFactory build() {
			return new WebDriverFactoryImpl(this)
		}
	}
}
