package com.kazurayam.webdriverfactory.firefox

import com.kazurayam.webdriverfactory.PreferencesModifier
import com.kazurayam.webdriverfactory.PreferencesModifierBase

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

enum FirefoxPreferencesModifiers {


	static PreferencesModifier downloadIntoUserHomeDownloadsDirectory() {
		Path p = Paths.get(System.getProperty('user.home'), 'Downloads')
		return downloadIntoDirectory(p)
	}

	static PreferencesModifier downloadIntoDirectory(Path directory) {
		Objects.requireNonNull(directory)
		if (!Files.exists(directory)) {
			println "created ${directory}"
			Files.createDirectories(directory)
		}
		PreferencesModifier pm = new PreferencesModifierBase(
				PreferencesModifier.Type.FIREFOX_downloadIntoDirectory,
				{ Map<String, Object> preferences ->
					preferences.put("browser.download.useDownloadDir", true)
					preferences.put("browser.download.folderList", 2)
					Path downloads = Paths.get(System.getProperty('user.home'), 'Downloads')
					preferences.put("browser.download.dir", downloads.toString())
					return preferences
				}
		)
		return pm
	}

	static PreferencesModifier downloadWithoutPrompt() {
		PreferencesModifier pm = new PreferencesModifierBase(
				PreferencesModifier.Type.FIREFOX_downloadWithoutPrompt,
				{ Map<String, Object> preferences ->
					// set preference not to show file download confirmation dialog
					def mimeTypes = getAllMimeTypesAsString()
					//println "mimeTypes=${mimeTypes}"
					preferences.put("browser.helperApps.neverAsk.saveToDisk", mimeTypes)
					preferences.put("browser.helperApps.neverAsk.openFile", mimeTypes)
					return preferences
				})
		return pm
	}

	private static final String getAllMimeTypesAsString() {
		return [
			"application/gzip",
			"application/java-archive",
			"application/json",
			"application/msexcel",
			"application/msword",
			"application/octet-stream",
			"application/pdf",
			"application/vnd-ms-office",
			"application/vnd-xls",
			"application/vnd.ms-excel",
			"application/vnd.ms-powerpoint",
			"application/vnd.openxmlformats-officedocument.presentationml.presentation",
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
			"application/x-dos_mx_excel",
			"application/x-excel",
			"application/x-ms-excel",
			"application/x-msexcel",
			"application/x-tar",
			"application/x-xls",
			"application/x-zip-compressed",
			"application/xls",
			"application/xml",
			"application/zip",
			"application/zlib",
			"image/bmp",
			"image/gif",
			"image/jpeg",
			"image/png",
			"image/svg+xml",
			"text/csv",
			"text/plain",
			"text/xml"
		].join(",")
	}

}
