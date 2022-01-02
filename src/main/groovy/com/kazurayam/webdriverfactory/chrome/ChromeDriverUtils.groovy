package com.kazurayam.webdriverfactory.chrome

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import com.kazurayam.webdriverfactory.utils.OSIdentifier

class ChromeDriverUtils {

	static final String LOG_FILE_NAME = 'chromedriver.log'

	/**
	 * let ChromeDriver to make verbose log into the logsDir
	 *
	 * @param logsDir
	 */
	static void enableChromeDriverLog(Path logsDir) {
		Files.createDirectories(logsDir)
		Path chromeDriverLog = logsDir.resolve(LOG_FILE_NAME)
		System.setProperty('webdriver.chrome.logfile', chromeDriverLog.toString())
		System.setProperty("webdriver.chrome.verboseLogging", "true")
	}

	/**
	 * returns the path of the binary of Chrome browser
	 *
	 * https://github.com/SeleniumHQ/selenium/wiki/ChromeDriver#requirements
	 *
	 * No more used. we will to delegate to the binogracia/webdrivermanager
	 */
	@Deprecated
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
}
