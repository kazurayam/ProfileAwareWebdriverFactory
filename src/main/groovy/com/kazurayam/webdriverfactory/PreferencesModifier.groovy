package com.kazurayam.webdriverfactory

interface PreferencesModifier {

	enum Type {
		disableViewersOfFlashAndPdf,
		downloadIntoDirectory,
		downloadIntoUserHomeDownloadsDirectory,
		downloadWithoutPrompt,
	}

	Type getType()

	Map<String, Object> modify(Map<String, Object> preferences)
}
