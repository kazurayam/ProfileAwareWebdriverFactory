package com.kazurayam.webdriverfactory.firefox

import org.openqa.selenium.firefox.FirefoxOptions

public class FirefoxOptionsModifierHeadless implements FirefoxOptionsModifier {

	FirefoxOptions modify(FirefoxOptions options) {
		Objects.requireNonNull(options)
		options.addArguments("-headless");
		return options
	}
}
