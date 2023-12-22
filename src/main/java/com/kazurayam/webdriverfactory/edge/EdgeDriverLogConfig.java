package com.kazurayam.webdriverfactory.edge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EdgeDriverLogConfig {

    public static void enableChromeDriverLog(Path logsDir) throws IOException {
        Files.createDirectories(logsDir);
        Path chromeDriverLog = logsDir.resolve(LOG_FILE_NAME);
        System.setProperty("webdriver.chrome.logfile", chromeDriverLog.toString());
        System.setProperty("webdriver.chrome.verboseLogging", "true");
    }

    public static String getLOG_FILE_NAME() {
        return LOG_FILE_NAME;
    }

    private static final String LOG_FILE_NAME = "edgedriver.log";

}
