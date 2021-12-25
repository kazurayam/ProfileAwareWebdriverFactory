package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifier
import org.openqa.selenium.chrome.ChromeOptions

class ChromeOptionsModifierHeadless implements ChromeOptionsModifier {

	ChromeOptions modify(ChromeOptions chromeOptions) {
		Objects.requireNonNull(chromeOptions, "chromeOptions must not be null")

		chromeOptions.addArguments("--headless")
		return chromeOptions
	}
}
