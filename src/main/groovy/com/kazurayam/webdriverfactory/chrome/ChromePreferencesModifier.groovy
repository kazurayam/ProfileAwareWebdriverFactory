package com.kazurayam.webdriverfactory.chrome

interface ChromePreferencesModifier {

	enum Type {
		disableViewersOfFlashAndPdf,
		downloadIntoDirectory,
		downloadIntoUserHomeDownloadsDirectory,
		downloadWithoutPrompt,
	}

	Type getType()

	Map<String, Object> modify(Map<String, Object> preferences)
}
