package com.kazurayam.webdriverfactory.chrome

import org.openqa.selenium.chrome.ChromeOptions

/**
 *
 * @author kazurayam
 *
 */
class ChromeOptionsBuilderImpl extends ChromeOptionsBuilder {

	private final Map<String, Object> preferences

	ChromeOptionsBuilderImpl() {
		this(new HashMap<String, Object>())
	}

	ChromeOptionsBuilderImpl(Map<String, Object> preferences) {
		this.preferences = preferences
	}

	@Override
	ChromeOptions build() {
		ChromeOptions options = new ChromeOptions()

		// set location of the Chrome Browser's binary
		//options.setBinary(ChromeDriverUtils.getChromeBinaryPath().toString());

		// set my chrome preferences
		options.setExperimentalOption('prefs', this.preferences)

		return options
	}
}