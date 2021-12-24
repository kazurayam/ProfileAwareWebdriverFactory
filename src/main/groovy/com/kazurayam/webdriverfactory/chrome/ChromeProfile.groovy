package com.kazurayam.webdriverfactory.chrome

import java.nio.file.Files
import java.nio.file.Path

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

/**
 * A representation of a Chrome Profile instance.
 * 
 * @author kazurayam
 */
class ChromeProfile implements Comparable<ChromeProfile> {

	static final String PREFERENCES_FILE_NAME = 'Preferences'

	private Path profilePath_ = null
	private def preferences_ = null
	private String name_ = null
	private String directoryName_ = null

	ChromeProfile(Path profilePath) {
		Path preferences = profilePath.resolve(PREFERENCES_FILE_NAME)
		if (!Files.exists(preferences)) {
			throw new IOException("${preferences.toString()} is not found")
		}
		this.profilePath_ = profilePath
		this.directoryName_ = profilePath.getFileName().toString()
		JsonSlurper slurper = new JsonSlurper()
		this.preferences_ = slurper.parse(preferences.toFile())
		this.name_ = this.preferences_['profile']['name']

		if (this.directoryName_ == 'Profile 2') {
			File out = new File('./out.json')
			out.text = JsonOutput.prettyPrint(JsonOutput.toJson(this.preferences_))
		}
	}

	String getName() {
		return this.name_
	}

	String getDirectoryName() {
		return this.directoryName_
	}

	def getPreferences() {
		return this.preferences_
	}

	Path getProfilePath() {
		return this.profilePath_
	}

	@Override
	int compareTo(ChromeProfile other) {
		return this.getName().compareTo(other.getName())
	}
}
