package com.kazurayam.webdriverfactory.desiredcapabilities

import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.DesiredCapabilities

interface DesiredCapabilitiesBuilder {

	DesiredCapabilities build(ChromeOptions chromeOptions)
	
	DesiredCapabilities build(FirefoxOptions chromeOptions)
	
}