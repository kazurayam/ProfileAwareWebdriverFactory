package com.kazurayam.webdriverfactory.firefox

interface PreferencesModifier {

	Map<String, Object> modify(Map<String, Object> preferences)
}
