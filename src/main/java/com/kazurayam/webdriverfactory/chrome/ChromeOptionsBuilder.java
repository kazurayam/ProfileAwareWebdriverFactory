package com.kazurayam.webdriverfactory.chrome;

import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Map;

public abstract class ChromeOptionsBuilder {

	public static ChromeOptionsBuilder newInstance() {
		return new ChromeOptionsBuilderImpl();
	}

	public static ChromeOptionsBuilder newInstance(Map<String, Object> chromePreferences) {
		return new ChromeOptionsBuilderImpl(chromePreferences);
	}

	public abstract ChromeOptions build();
}
