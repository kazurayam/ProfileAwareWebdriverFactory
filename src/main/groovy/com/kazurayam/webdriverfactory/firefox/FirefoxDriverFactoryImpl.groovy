package com.kazurayam.webdriverfactory.firefox


import java.nio.file.Paths

import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.WebDriver
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesModifier
import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesBuilderImpl

import groovy.json.JsonOutput

class FirefoxDriverFactoryImpl extends FirefoxDriverFactory {

	static Logger logger_ = LoggerFactory.getLogger(FirefoxDriverFactoryImpl.class)

	static {
		// dynamically add toJsonText() method to the built-in classes
		DesiredCapabilities.metaClass.toString = {
			return JsonOutput.prettyPrint(JsonOutput.toJson(delegate.asMap()))
		}
	}

	private final List<PreferencesModifier> firefoxPreferencesModifiers_
	private final List<FirefoxOptionsModifier> firefoxOptionsModifiers_
	private final List<DesiredCapabilitiesModifier> desiredCapabilitiesModifiers_

	private DesiredCapabilities desiredCapabilities_

	FirefoxDriverFactoryImpl() {
		firefoxPreferencesModifiers_  = new ArrayList<>();
		firefoxOptionsModifiers_      = new ArrayList<>();
		desiredCapabilitiesModifiers_ = new ArrayList<>();
		desiredCapabilities_ = null;
	}

	@Override
	void addFirefoxPreferencesModifier(PreferencesModifier firefoxPreferencesModifier) {
		firefoxPreferencesModifiers_.add(firefoxPreferencesModifier)
	}

	@Override
	void addFirefoxOptionsModifier(FirefoxOptionsModifier firefoxOptionsModifier) {
		firefoxOptionsModifiers_.add(firefoxOptionsModifier)
	}

	@Override
	void addDesiredCapabilitiesModifier(DesiredCapabilitiesModifier desiredCapabilitiesModifier) {
		desiredCapabilitiesModifiers_.add(desiredCapabilitiesModifier)
	}

	/**
	 * 1. enable logging by FirefoxDriver into the tmp directory under the Katalon Studio Project directory
	 * 2. ensure the path of FirefoxDriver executable
	 */
	private void prepare() {
		FirefoxDriverUtils.enableFirefoxDriverLog(Paths.get(".").resolve('tmp'))

		this.addFirefoxPreferencesModifier(new PreferencesModifierDefault())
		this.addFirefoxOptionsModifier(new FirefoxOptionsModifierDefault())
	}

	/**
	 * Create an instance of Gecko Driver with configuration
	 * setup through the chain of
	 *     Firefox Prefereces => Firefox Options => Desired Capabilities
	 * while modifying each containers with specified Modifiers
	 */
	private WebDriver execute() {
		// create a Firefox Preferences object as the seed
		Map<String, Object> firefoxPreferences = new HashMap<>()

		// modify the Chrome Preferences
		for (PreferencesModifier modifier in firefoxPreferencesModifiers_) {
			firefoxPreferences = modifier.modify(firefoxPreferences)
		}

		// create Firefox Options taking over the Firefox Preferences
		FirefoxOptions firefoxOptions = new FirefoxOptionsBuilderImpl().build(firefoxPreferences)
		// modify the Firefox Options
		for (FirefoxOptionsModifier modifier in firefoxOptionsModifiers_) {
			firefoxOptions = modifier.modify(firefoxOptions)
		}

		// create Desired Capabilities taking over settings in the Chrome Options
		desiredCapabilities_ = new DesiredCapabilitiesBuilderImpl().build(firefoxOptions)
		// modify the Desired Capabilities
		for (DesiredCapabilitiesModifier dcm in desiredCapabilitiesModifiers_) {
			desiredCapabilities_ = dcm.modify(desiredCapabilities_)
		}

		// now launch the browser
		WebDriver driver = new FirefoxDriver(desiredCapabilities_)

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
	 * returns the DesiredCapabilitiy object employed when the factory instantiated ChromeDriver by calling execute().
	 * If you call this before calling execute(), null will be returned.
	 */
	@Override
	DesiredCapabilities getEmployedDesiredCapabilities() {
		return this.desiredCapabilities_
	}

}
