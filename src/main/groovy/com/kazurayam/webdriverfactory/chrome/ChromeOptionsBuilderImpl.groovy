package com.kazurayam.webdriverfactory.chrome

import org.openqa.selenium.chrome.ChromeOptions

/**
 *
 * @author kazurayam
 *
 */
class ChromeOptionsBuilderImpl implements ChromeOptionsBuilder {

	/**
	 *
	 */
	@Override
	ChromeOptions build(Map<String, Object> chromePreferences) {
		ChromeOptions options = new ChromeOptions()
		// set location of the Chrome Browser's binary
		options.setBinary(ChromeDriverUtils.getChromeBinaryPath().toString());
		// set my chrome preferences
		options.setExperimentalOption('prefs', chromePreferences)
		// The following lines are copy&pasted from
		// https://github.com/SeleniumHQ/selenium/issues/4961

		return options
	}
}