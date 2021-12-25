package com.kazurayam.webdriverfactory

import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers

// import com.kazurayam.webdriverfactory.firefox.FirefoxDriverFactory
// import com.kazurayam.webdriverfactory.firefox.FirefoxOptionsModifier
// import com.kazurayam.webdriverfactory.firefox.FirefoxOptionsModifierHeadless

import org.openqa.selenium.WebDriver


class WebDriverFactory {

	static WebDriver newWebDriver(DriverTypeName driverTypeName) {
		switch (driverTypeName) {
			case DriverTypeName.CHROME_DRIVER :		// Google Chrome Browser
				return ChromeDriverFactory.newInstance().newChromeDriver()
				break
			case DriverTypeName.HEADLESS_DRIVER :	// Chrome Headless Browser
				ChromeDriverFactory cdf = ChromeDriverFactory.newInstance()
				cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
				return cdf.newChromeDriver()
				break
			// I haven't worked out enough for Firefox yet
			//case DriverTypeName.FIREFOX_DRIVER:		// Firefox Browser
			//	FirefoxDriverFactory fdf = FirefoxDriverFactory.newInstance()
				// FirefoxOptionsModifier is not yet implemented
			//	return fdf.newFirefoxDriver()
			default:
				throw new RuntimeException("DriverType ${driverTypeName} is not supported")
		}
	}

	static WebDriver newWebDriver(DriverTypeName driverTypeName, String userProfile) {
		return newWebDriver(driverTypeName, new UserProfile(userProfile))
	}

	static WebDriver newWebDriver(DriverTypeName driverTypeName, UserProfile userProfile) {
		switch (driverTypeName) {
			case DriverTypeName.CHROME_DRIVER :
				return ChromeDriverFactory.newInstance().newChromeDriverWithUserProfile(userProfile)
				break
			case DriverTypeName.HEADLESS_DRIVER :	// Chrome Headless Browser
				ChromeDriverFactory cdf = ChromeDriverFactory.newInstance()
				cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
				return cdf.newChromeDriverWithUserProfile(userProfile)
				break
			//case DriverTypeName.FIREFOX_DRIVER :
			//	return FirefoxDriverFactory.newInstance().newFirefoxDriverWithProfile(profileName)
			//	break
			//case DriverTypeName.FIREFOX_HEADLESS_DRIVER :	// Chrome Headless Browser
			//	FirefoxDriverFactory cdf = FirefoxDriverFactory.newInstance()
			//	FirefoxOptionsModifier com = new FirefoxOptionsModifierHeadless()
			//	cdf.addFirefoxOptionsModifier(com)
			//	return cdf.newFirefoxDriverWithProfile(profileName)
			//	break

			default:
				throw new RuntimeException("DriverType ${driverTypeName} is not supported")
		}
	}
}
