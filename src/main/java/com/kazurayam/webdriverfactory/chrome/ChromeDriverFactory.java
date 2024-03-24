package com.kazurayam.webdriverfactory.chrome;

import com.kazurayam.webdriverfactory.CacheDirectoryName;
import com.kazurayam.webdriverfactory.UserProfile;
import com.kazurayam.webdriverfactory.WebDriverFactoryException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public abstract class ChromeDriverFactory {
	public static ChromeDriverFactory newChromeDriverFactory() throws IOException {
		return new ChromeDriverFactoryImpl();
	}

	public static ChromeDriverFactory newChromeDriverFactory(boolean requireDefaultSettings)
			throws IOException {
		return new ChromeDriverFactoryImpl(requireDefaultSettings);
	}

	public static ChromeDriverFactory newHeadlessChromeDriverFactory() throws IOException {
		ChromeDriverFactoryImpl cdfi = new ChromeDriverFactoryImpl();
		cdfi.addChromeOptionsModifier(ChromeOptionsModifiers.headless());
		return cdfi;
	}

	public static ChromeDriverFactory newHeadlessChromeDriverFactory(boolean requireDefaultSettings)
			throws IOException {
		ChromeDriverFactoryImpl cdfi = new ChromeDriverFactoryImpl(requireDefaultSettings);
		cdfi.addChromeOptionsModifier(ChromeOptionsModifiers.headless());
		return cdfi;
	}

	public abstract ChromeDriverFactory addChromePreferencesModifier(ChromePreferencesModifier chromePreferencesModifier);

	public abstract ChromeDriverFactory addAllChromePreferencesModifiers(List<ChromePreferencesModifier> chromePreferencesModifierList);

	public abstract ChromeDriverFactory addChromeOptionsModifier(ChromeOptionsModifier chromeOptionsModifier);

	public abstract ChromeDriverFactory addAllChromeOptionsModifiers(List<ChromeOptionsModifier> chromeOptionsModifierList);

	public abstract ChromeDriverFactory pageLoadTimeout(Integer waitSeconds);

	public abstract LaunchedChromeDriver newChromeDriver();

	public abstract LaunchedChromeDriver newChromeDriver(UserProfile userProfile) throws IOException, WebDriverFactoryException;

	public abstract LaunchedChromeDriver newChromeDriver(UserProfile userProfile, UserDataAccess instruction) throws IOException, WebDriverFactoryException;

	public abstract LaunchedChromeDriver newChromeDriver(CacheDirectoryName cacheDirectoryName) throws IOException, WebDriverFactoryException;

	public abstract LaunchedChromeDriver newChromeDriver(CacheDirectoryName cacheDirectoryName, UserDataAccess instruction) throws IOException, WebDriverFactoryException;

	public abstract void enableChromeDriverLog(Path outputDirectory) throws IOException;

	/**
	 * @param chromeDriverPath the path of the chromedriver binary
	 */
	public static void setPathToChromeDriverExecutable(String chromeDriverPath) {
		Objects.requireNonNull(chromeDriverPath);
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
	}

	public static enum UserDataAccess {
		FOR_HERE, TO_GO;
	}
}
