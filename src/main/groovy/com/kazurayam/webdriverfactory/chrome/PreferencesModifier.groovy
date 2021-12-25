package com.kazurayam.webdriverfactory.chrome

interface PreferencesModifier {

	Map<String, Object> modify(Map<String, Object> preferences)
}
