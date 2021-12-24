package com.kazurayam.webdriverfactory.firefox

import org.openqa.selenium.firefox.FirefoxOptions

public class FirefoxOptionsBuilderImpl implements FirefoxOptionsBuilder {

	/**
	 *
	 */
	@Override
	FirefoxOptions build(Map<String, Object> firefoxPreferences) {
		FirefoxOptions options = new FirefoxOptions()
		// set location of the Chrome Browser's binary
		options.setBinary(FirefoxDriverUtils.getFirefoxBinaryPath().toString());
		// set my chrome preferences
		//options.setExperimentalOption('prefs', firefoxPreferences)
		// The following lines are copy&pasted from
		// https://github.com/SeleniumHQ/selenium/issues/4961

		return options
	}
}
