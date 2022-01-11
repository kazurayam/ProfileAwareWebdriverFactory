package com.kazurayam.webdriverfactory.chrome

import org.openqa.selenium.chrome.ChromeOptions

interface ChromeOptionsModifier {

	enum Type {
		disableDevShmUsage,
		disableExtensions,
		disableGpu,
		disableInfobars,
		headless,
		incognito,
		noSandbox,
		singleProcess,
		windowSize,
		windowSize1024_768,
		withProfileDirectoryName
	}

	Type getType()
	ChromeOptions modify(ChromeOptions chromeOptions)
}
