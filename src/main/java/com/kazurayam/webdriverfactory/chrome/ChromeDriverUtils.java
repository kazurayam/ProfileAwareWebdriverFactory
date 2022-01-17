package com.kazurayam.webdriverfactory.chrome;

import com.kazurayam.webdriverfactory.utils.OSIdentifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChromeDriverUtils {
    /**
     * let ChromeDriver to make verbose log into the logsDir
     *
     * @param logsDir the path of a directory where the log file should be located
     * @throws IOException when failed to create the directory
     */
    public static void enableChromeDriverLog(Path logsDir) throws IOException {
        Files.createDirectories(logsDir);
        Path chromeDriverLog = logsDir.resolve(LOG_FILE_NAME);
        System.setProperty("webdriver.chrome.logfile", chromeDriverLog.toString());
        System.setProperty("webdriver.chrome.verboseLogging", "true");
    }

    /**
     * returns the path of the binary of Chrome browser
     * <p>
     * https://github.com/SeleniumHQ/selenium/wiki/ChromeDriver#requirements
     * <p>
     * No more used. we will to delegate to the binogracia/webdrivermanager
     *
     * @return the path of chromedirever executable according to the type of OS
     */
    @Deprecated
    public static Path getChromeBinaryPath() {
        if (OSIdentifier.isWindows()) {
            // "C:\Program Files (x86)\Google\Chrome\Application\chrome.exe"
            return Paths.get("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
        } else if (OSIdentifier.isMac()) {
            return Paths.get("/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
        } else if (OSIdentifier.isUnix()) {
            return Paths.get("/usr/bin/google-chrome");
        } else {
            throw new IllegalStateException("Windows, Mac, Linux are supported. Other platforms are not supported");
        }

    }

    public static String getLOG_FILE_NAME() {
        return LOG_FILE_NAME;
    }

    private static final String LOG_FILE_NAME = "chromedriver.log";
}
