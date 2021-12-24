package com.kazurayam.webdriverfactory.chrome

import org.openqa.selenium.chrome.ChromeOptions

interface ChromeOptionsBuilder {

	ChromeOptions build(Map<String, Object> chromePreferences)
}