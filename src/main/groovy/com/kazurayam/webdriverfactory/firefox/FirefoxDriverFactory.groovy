package com.kazurayam.webdriverfactory.firefox

import com.kazurayam.webdriverfactory.PreferencesModifier
import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.ProfileDirectoryName
import java.nio.file.Path

abstract class FirefoxDriverFactory {

	static FirefoxDriverFactory newFirefoxDriverFactory() {
		return new FirefoxDriverFactoryImpl()
	}

	static FirefoxDriverFactory newFirefoxDriverFactory(boolean requireDefaultSettings) {
		return new FirefoxDriverFactoryImpl(requireDefaultSettings)
	}

	static FirefoxDriverFactory newHeadlessFirefoxDriverFactory() {
		FirefoxDriverFactoryImpl fdfi = new FirefoxDriverFactoryImpl()
		fdfi.addFirefoxOptionsModifier(FirefoxOptionsModifiers.headless())
		return fdfi
	}

	static FirefoxDriverFactory newHeadlessFirefoxDriverFactory(boolean requireDefaultSettings) {
		FirefoxDriverFactoryImpl fdfi = new FirefoxDriverFactoryImpl(requireDefaultSettings)
		fdfi.addFirefoxOptionsModifier(FirefoxOptionsModifiers.headless())
		return fdfi
	}

	abstract void addFirefoxPreferencesModifier(
			PreferencesModifier firefoxPreferencesModifier)

	abstract void addAllFirefoxPreferencesModifier(
			List<PreferencesModifier> firefoxPreferencesModifierList)

	abstract void addFirefoxOptionsModifier(
			FirefoxOptionsModifier firefoxOptionsModifier)

	abstract void addAllFirefoxOptionsModifier(
			List<FirefoxOptionsModifier> firefoxOptionsModifierList)

	abstract void pageLoadTimeout(Integer waitSeconds)

	abstract LaunchedFirefoxDriver newFirefoxDriver()

	abstract LaunchedFirefoxDriver newFirefoxDriver(UserProfile userProfile)

	abstract LaunchedFirefoxDriver newFirefoxDriver(ProfileDirectoryName profileDirectoryName)

	abstract void enableFirefoxDriverLog(Path outputDirectory)

	static void setPathToFirefoxDriverExecutable(String geckoDriverPath) {
		Objects.requireNonNull(geckoDriverPath)
		System.setProperty("webdriver.gecko.driver", geckoDriverPath)
	}
}
