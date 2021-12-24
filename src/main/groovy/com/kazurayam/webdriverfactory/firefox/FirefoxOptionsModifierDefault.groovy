package com.kazurayam.webdriverfactory.firefox

import org.openqa.selenium.firefox.FirefoxOptions

public class FirefoxOptionsModifierDefault implements FirefoxOptionsModifier {

	FirefoxOptions modify(FirefoxOptions options) {
		Objects.requireNonNull(options)
		options.addArguments("--window-size=1280,1024");
		options.addArguments("--width=1280");
		options.addArguments("--height=1024");
		
		return options
	}
}
