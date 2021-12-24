package com.kazurayam.webdriverfactory.chrome

import java.nio.file.Path
import java.nio.file.Paths

class ChromePreferencesModifierDefault implements ChromePreferencesModifier {

	@Override
	Map<String, Object> modify(Map<String, Object> chromePreferences) {

		// Below two preference settings will disable popup dialog when download file
		chromePreferences.put('profile.default_content_settings.popups', 0)
		chromePreferences.put('download.prompt_for_download', false)

		// set directory to save files
		Path downloads = Paths.get(System.getProperty('user.home'), 'Downloads')
		chromePreferences.put('download.default_directory', downloads.toString())

		// disable flash and pdf viewer
		chromePreferences.put('plugins.plugins_disabled',
				[
					'Adobe Flash Player',
					'Chrome PDF Viewer'
				])
		return chromePreferences
	}
}
