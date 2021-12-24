package com.kazurayam.webdriverfactory.chrome

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import com.kazurayam.webdriverfactory.utils.OSIdentifier

class ChromeDriverUtils {

	/**
	 * let ChromeDriver to make verbose log into the logsDir
	 *
	 * @param logsDir
	 */
	static void enableChromeDriverLog(Path logsDir) {
		Files.createDirectories(logsDir)
		Path chromeDriverLog = logsDir.resolve('chromedriver.log')
		System.setProperty('webdriver.chrome.logfile', chromeDriverLog.toString())
		System.setProperty("webdriver.chrome.verboseLogging", "true")
	}

	/**
	 * returns the path of the binary of Chrome browser
	 *
	 * https://github.com/SeleniumHQ/selenium/wiki/ChromeDriver#requirements
	 */
	static Path getChromeBinaryPath() {
		if (OSIdentifier.isWindows()) {
			// "C:\Program Files (x86)\Google\Chrome\Application\chrome.exe"
			return Paths.get("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe")
		} else if (OSIdentifier.isMac()) {
			return Paths.get('/Applications/Google Chrome.app/Contents/MacOS/Google Chrome')
		} else if (OSIdentifier.isUnix()) {
			return Paths.get('/usr/bin/google-chrome')
		} else {
			throw new IllegalStateException(
			"Windows, Mac, Linux are supported. Other platforms are not supported")
		}
	}

	/**
	 * as described https://chromium.googlesource.com/chromium/src/+/HEAD/docs/user_data_dir.md
	 *
	 * @returns the path of 'UserData' directory in which Google Chrome's Profile directories are located
	 */
	static Path getChromeUserDataDirectory() {
		return ChromeProfileFinder.getChromeUserDataDirectory()
	}

	/**
	 * @returns the path of directory in which Chrome Profile of 'name' is located
	 */
	static Path getChromeProfileDirectory(String name) {
		Objects.requireNonNull(name, "name must not be null")
		ChromeProfile cProfile = ChromeProfileFinder.getChromeProfile(name)
		if (cProfile != null) {
			return cProfile.getProfilePath()
		} else {
			return null
		}
	}
}
