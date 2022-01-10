package com.kazurayam.webdriverfactory.firefox


import java.nio.file.Paths

import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.WebDriver
import org.slf4j.Logger
import org.slf4j.LoggerFactory


import groovy.json.JsonOutput

class FirefoxDriverFactoryImpl extends FirefoxDriverFactory {

	static Logger logger_ = LoggerFactory.getLogger(FirefoxDriverFactoryImpl.class)

	private final List<FirefoxPreferencesModifier> firefoxPreferencesModifiers_
	private final List<FirefoxOptionsModifier> firefoxOptionsModifiers_

	private FirefoxOptions employedOptions_

	FirefoxDriverFactoryImpl() {
		firefoxPreferencesModifiers_  = new ArrayList<>();
		firefoxOptionsModifiers_      = new ArrayList<>();
	}

	@Override
	void addFirefoxPreferencesModifier(FirefoxPreferencesModifier firefoxPreferencesModifier) {
		firefoxPreferencesModifiers_.add(firefoxPreferencesModifier)
	}

	@Override
	void addFirefoxOptionsModifier(FirefoxOptionsModifier firefoxOptionsModifier) {
		firefoxOptionsModifiers_.add(firefoxOptionsModifier)
	}

	/**
	 * 1. enable logging by FirefoxDriver into the tmp directory under the current working directory
	 * 2. ensure the path of FirefoxDriver executable
	 */
	private void prepare() {
		FirefoxDriverUtils.enableFirefoxDriverLog(Paths.get(".").resolve('tmp'))

		this.addFirefoxPreferencesModifier(new FirefoxPreferencesModifierDefault())
		this.addFirefoxOptionsModifier(new FirefoxOptionsModifierDefault())
	}

	/**
	 * Create an instance of Gecko Driver with configuration
	 * setup through the chain of
	 *     Firefox Prefereces => Firefox Options
	 * while modifying each containers with specified Modifiers
	 */
	private WebDriver execute() {
		// create a Firefox Preferences object as the seed
		Map<String, Object> firefoxPreferences = new HashMap<>()

		// modify the Chrome Preferences
		for (FirefoxPreferencesModifier modifier in firefoxPreferencesModifiers_) {
			firefoxPreferences = modifier.modify(firefoxPreferences)
		}

		// create Firefox Options taking over the Firefox Preferences
		FirefoxOptions firefoxOptions = new FirefoxOptionsBuilderImpl().build(firefoxPreferences)
		// modify the Firefox Options
		for (FirefoxOptionsModifier modifier in firefoxOptionsModifiers_) {
			firefoxOptions = modifier.modify(firefoxOptions)
		}

		// now launch the browser
		WebDriver driver = new FirefoxDriver(firefoxOptions)

		// well done
		return driver
	}

	@Override
	WebDriver newFirefoxDriver() {
		this.prepare()
		return this.execute()
	}

	@Override
	WebDriver newFirefoxDriverWithProfile(String profileName) {
		Objects.requireNonNull(profileName, "profileName must not be null")
		this.prepare()
		//
		throw new RuntimeException("not yet implemented")
	}

	/**
	 * returns the FirefoxOptions object employed
	 * when the factory instantiated FirefoxDriver by calling execute().
	 * If you call this before calling execute(), null will be returned.
	 */
	@Override
	FirefoxOptions getEmployedOptions() {
		return this.employedOptions
	}

}
