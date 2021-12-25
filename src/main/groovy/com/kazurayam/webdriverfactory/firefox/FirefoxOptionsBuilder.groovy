package com.kazurayam.webdriverfactory.firefox

import org.openqa.selenium.firefox.FirefoxOptions

abstract class FirefoxOptionsBuilder {

	protected Map<String, Object> preferences

	static FirefoxOptionsBuilder newInstance() {
		return new FirefoxOptionsBuilderImpl()
	}

	static FirefoxOptionsBuilder newInstance(Map<String, Object> preferences) {
		return new FirefoxOptionsBuilderImpl(preferences)
	}

	abstract FirefoxOptions build()
}
