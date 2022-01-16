package com.kazurayam.webdriverfactory.chrome;

import org.openqa.selenium.chrome.ChromeOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kazurayam
 */
public class ChromeOptionsBuilderImpl extends ChromeOptionsBuilder {

	public ChromeOptionsBuilderImpl() {
		this(new HashMap<String, Object>());
	}

	public ChromeOptionsBuilderImpl(Map<String, Object> preferences) {
		this.preferences = preferences;
	}

	@Override
	public ChromeOptions build() {
		ChromeOptions options = new ChromeOptions();
		options.setExperimentalOption("prefs", this.preferences);
		return options;
	}

	private final Map<String, Object> preferences;
}
