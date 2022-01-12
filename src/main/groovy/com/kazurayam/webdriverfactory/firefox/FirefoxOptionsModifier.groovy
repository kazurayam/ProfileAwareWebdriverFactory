package com.kazurayam.webdriverfactory.firefox

import org.openqa.selenium.firefox.FirefoxOptions

interface FirefoxOptionsModifier {

	enum Type {
		headless,
		windowSize,
		windowSize1024x768,
		withProfile,
		withProfileDirectoryName,
	}

	Type getType()

	FirefoxOptions modify(FirefoxOptions firefoxOptions)
}
