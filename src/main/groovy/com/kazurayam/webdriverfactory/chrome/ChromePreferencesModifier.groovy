package com.kazurayam.webdriverfactory.chrome

interface ChromePreferencesModifier {

	Map<String, Object> modify(Map<String, Object> chromePreferences)
}
