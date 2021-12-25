package com.kazurayam.webdriverfactory.chrome

import org.openqa.selenium.chrome.ChromeOptions

abstract class ChromeOptionsBuilder {

	static ChromeOptionsBuilder newInstance() {
		return new ChromeOptionsBuilderImpl()
	}

	static ChromeOptionsBuilder newInstance(Map<String, Object> chromePreferences) {
		return new ChromeOptionsBuilderImpl(chromePreferences)
	}

	abstract ChromeOptions build()
}