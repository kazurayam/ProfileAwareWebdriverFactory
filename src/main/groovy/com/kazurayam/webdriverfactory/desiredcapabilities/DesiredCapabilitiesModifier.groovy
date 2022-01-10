package com.kazurayam.webdriverfactory.desiredcapabilities

import org.openqa.selenium.remote.DesiredCapabilities

interface DesiredCapabilitiesModifier {

	enum Type {
		browserName,
		passThrough,
	}

	Type getType()

	DesiredCapabilities modify(DesiredCapabilities desiredCapabilities)
}
