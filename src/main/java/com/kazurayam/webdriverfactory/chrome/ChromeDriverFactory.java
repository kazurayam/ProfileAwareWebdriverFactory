package com.kazurayam.webdriverfactory.chrome;

import com.kazurayam.webdriverfactory.PreferencesModifier;
import com.kazurayam.webdriverfactory.ProfileDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public abstract class ChromeDriverFactory {
	public static ChromeDriverFactory newChromeDriverFactory() {
		return new ChromeDriverFactoryImpl();
	}

	public static ChromeDriverFactory newChromeDriverFactory(boolean requireDefaultSettings) {
		return new ChromeDriverFactoryImpl(requireDefaultSettings);
	}

	public static ChromeDriverFactory newHeadlessChromeDriverFactory() {
		ChromeDriverFactoryImpl cdfi = new ChromeDriverFactoryImpl();
		cdfi.addChromeOptionsModifier(ChromeOptionsModifier.headless);
		return cdfi;
	}

	public static ChromeDriverFactory newHeadlessChromeDriverFactory(boolean requireDefaultSettings) {
		ChromeDriverFactoryImpl cdfi = new ChromeDriverFactoryImpl(requireDefaultSettings);
		cdfi.addChromeOptionsModifier(ChromeOptionsModifier.headless);
		return cdfi;
	}

	public abstract void addChromePreferencesModifier(PreferencesModifier chromePreferencesModifier);

	public abstract void addAllChromePreferencesModifiers(List<PreferencesModifier> chromePreferencesModifierList);

	public abstract void addChromeOptionsModifier(ChromeOptionsModifyFunction chromeOptionsModifier);

	public abstract void addAllChromeOptionsModifiers(List<ChromeOptionsModifyFunction> chromeOptionsModifierList);

	public abstract void pageLoadTimeout(Integer waitSeconds);

	public abstract LaunchedChromeDriver newChromeDriver();

	public abstract LaunchedChromeDriver newChromeDriver(UserProfile userProfile);

	public abstract LaunchedChromeDriver newChromeDriver(UserProfile userProfile, UserDataAccess instruction);

	public abstract LaunchedChromeDriver newChromeDriver(ProfileDirectoryName profileDirectoryName);

	public abstract LaunchedChromeDriver newChromeDriver(ProfileDirectoryName profileDirectoryName, UserDataAccess instruction);

	public abstract void enableChromeDriverLog(Path outputDirectory);

	/**
	 *
	 */
	public static void setPathToChromeDriverExecutable(String chromeDriverPath) {
		Objects.requireNonNull(chromeDriverPath);
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
	}

	public static enum UserDataAccess {
		FOR_HERE, TO_GO;
	}
}
