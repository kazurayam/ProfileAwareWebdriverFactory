package com.kazurayam.webdriverfactory

interface PreferencesModifier {

	enum Type {
		CHROME_disableViewersOfFlashAndPdf,
		CHROME_downloadIntoDirectory,
		CHROME_downloadIntoUserHomeDownloadsDirectory,
		CHROME_downloadWithoutPrompt,
		FIREFOX_downloadIntoDirectory,
		FIREFOX_downloadIntoUserHomeDownloadsDirectory,
		FIREFOX_downloadWithoutPrompt,

	}

	Type getType()

	Map<String, Object> modify(Map<String, Object> preferences)
}
