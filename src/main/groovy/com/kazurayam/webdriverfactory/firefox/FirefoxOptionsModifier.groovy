package com.kazurayam.webdriverfactory.firefox

import org.openqa.selenium.firefox.FirefoxOptions

interface FirefoxOptionsModifier {

	enum Type {
		headless,
		windowSize,
		withProfileDirectoryName,
	}

	Type getType()

	FirefoxOptions modify(FirefoxOptions firefoxOptions)
}
