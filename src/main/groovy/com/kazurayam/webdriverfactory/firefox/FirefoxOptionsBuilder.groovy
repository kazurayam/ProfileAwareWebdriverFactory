package com.kazurayam.webdriverfactory.firefox

import org.openqa.selenium.firefox.FirefoxOptions

interface FirefoxOptionsBuilder {

	FirefoxOptions build(Map<String, Object> firefoxPreferences)
}
