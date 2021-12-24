package com.kazurayam.webdriverfactory.firefox

import java.nio.file.Path
import java.nio.file.Paths

public class FirefoxPreferencesModifierDefault implements FirefoxPreferencesModifier {

	@Override
	Map<String, Object> modify(Map<String, Object> firefoxPreferences) {
		Objects.requireNonNull(firefoxPreferences)

		// set location to store files after downloading
		firefoxPreferences.put("browser.download.useDownloadDir", true)
		firefoxPreferences.put("browser.download.folderList", 2)
		Path downloads = Paths.get(System.getProperty('user.home'), 'Downloads')
		firefoxPreferences.put("browser.download.dir", downloads.toString())

		// set preference not to show file download confirmation dialog
		def mimeTypes = getAllMimeTypesAsString()
		//println "mimeTypes=${mimeTypes}"
		firefoxPreferences.put("browser.helperApps.neverAsk.saveToDisk", mimeTypes)
		firefoxPreferences.put("browser.helperApps.neverAsk.openFile", mimeTypes)

		// profile.setPreference("browser.download.manager.showWhenStarting", false) // you can not modify this particular profile any more
		firefoxPreferences.put("pdfjs.disable", true)

		return firefoxPreferences
	}

	private static String getAllMimeTypesAsString() {
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
