package com.kazurayam.webdriverfactory.desiredcapabilities

import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities

class DesiredCapabilitiesBuilderImpl implements DesiredCapabilitiesBuilder {

	/**
	 *
	 */
	@Override
	DesiredCapabilities build(ChromeOptions chromeOptions) {
		DesiredCapabilities cap = DesiredCapabilities.chrome()
		cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)
		cap.setCapability(ChromeOptions.CAPABILITY, chromeOptions)
		return cap
	}
	
	/**
	 *
	 */
	@Override
	DesiredCapabilities build(FirefoxOptions firefoxOptions) {
		DesiredCapabilities cap = DesiredCapabilities.chrome()
		cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true)
		cap.setCapability(ChromeOptions.CAPABILITY, firefoxOptions)
		return cap
	}
}
