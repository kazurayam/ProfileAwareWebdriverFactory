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

		// set location of the Chrome Browser's binary
		//options.setBinary(ChromeDriverUtils.getChromeBinaryPath().toString());

		// set my chrome preferences
		// Selenium3 and Selenium4 are different here.

		// FIXME: here is an issue concerning Selenium3 vs Selenium4 incompatibility
		//
		// code for Selenium 4
		// setExperimentalOption() is available only in Selenium 4
		//options.setExperimentalOption("prefs", this.preferences);

		// code for Selenium 3
		// https://www.javadoc.io/doc/org.seleniumhq.selenium/selenium-chrome-driver/3.141.59/org/openqa/selenium/chrome/ChromeOptions.html#ChromeOptions--
		preferences.forEach((k,v) -> {
			options.addArguments(String.format("%s=%s",k,v));
		});

		// I should no longer use Preferences. Live with only ChromeOptions

		return options;
	}

	private final Map<String, Object> preferences;
}
