package com.kazurayam.webdriverfactory;

import java.util.Map;

public interface PreferencesModifier {
    public abstract Type getType();

    public abstract Map<String, Object> modify(Map<String, Object> preferences);

    public static enum Type {
        CHROME_disableViewersOfFlashAndPdf, CHROME_downloadIntoDirectory, CHROME_downloadIntoUserHomeDownloadsDirectory, CHROME_downloadWithoutPrompt, FIREFOX_downloadIntoDirectory, FIREFOX_downloadIntoUserHomeDownloadsDirectory, FIREFOX_downloadWithoutPrompt;
    }
}
