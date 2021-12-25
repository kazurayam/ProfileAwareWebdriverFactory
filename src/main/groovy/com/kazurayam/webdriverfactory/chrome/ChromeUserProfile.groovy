package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.UserProfile

import java.nio.file.Files
import java.nio.file.Path

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

/**
 * A representation of a Chrome Profile instance.
 * 
 * @author kazurayam
 */
class ChromeUserProfile implements Comparable<ChromeUserProfile> {

	static final String PREFERENCES_FILE_NAME = 'Preferences'

	private final Path userDataDirectory
	private final String profileDirectoryName

	private UserProfile userProfileName
	private String preferences

	/**
	 * @param userDataDirectory "~/Library/Application Support/Google/Chrome/"
	 * @param profileName "Default", "Profile 1", "Profile 2", "Profile 3", ...
	 */
    ChromeUserProfile(Path userDataDirectory, String profileDirectoryName) {
		Objects.requireNonNull(userDataDirectory)
		Objects.requireNonNull(profileDirectoryName)
		if (! Files.exists(userDataDirectory)) {
			throw new IllegalArgumentException("${userDataDirectory} is not found")
		}
		Path profilePath = userDataDirectory.resolve(profileDirectoryName)
		if (! Files.exists(profilePath)) {
			throw new IllegalArgumentException("${p} is not found")
		}
		this.userDataDirectory = userDataDirectory
		this.profileDirectoryName = profileDirectoryName

		Path preferencesPath = profilePath.resolve(PREFERENCES_FILE_NAME)
		if (!Files.exists(preferencesPath)) {
			throw new IOException("${preferencesPath} is not found")
		}
		this.preferences = JsonOutput.prettyPrint(preferencesPath.toFile().text)

		Map m = new JsonSlurper().parseText(this.preferences)
		String name = m['profile']['name']
		this.userProfileName = new UserProfile(name)
		assert this.userProfileName != null
	}

	Path getUserDataDirectory() {
		return this.userDataDirectory
	}

	ProfileDirectoryName getProfileDirectoryName() {
		return new ProfileDirectoryName(this.profileDirectoryName)
	}

	Path getChromeUserProfileDirectory() {
		return this.getUserDataDirectory().resolve(this.getProfileDirectoryName().getName())
	}

	UserProfile getUserProfileName() {
		return this.userProfileName
	}

	String getPreferences() {
		return this.preferences
	}

	/**
	 * order by UserProfileName
	 *
	 * @param other
	 * @return
	 */
	@Override
	int compareTo(ChromeUserProfile other) {
		return this.getUserProfileName() <=> other.getUserProfileName()
	}

	@Override
	boolean equals(Object obj) {
		if (! obj instanceof ChromeUserProfile) {
			return false
		}
		ChromeUserProfile other = (ChromeUserProfile)obj
		return this.getUserDataDirectory() == other.getUserDataDirectory() &&
				this.getProfileDirectoryName() == other.getProfileDirectoryName()
	}

	@Override
	int hashCode() {
		int hash = 7
		hash = 31 * hash + (int) this.getUserDataDirectory().hashCode()
		hash = 31 * hash + (int) this.getProfileDirectoryName().hashCode()
		return hash
	}

	@Override
	String toString() {
		StringBuilder sb = new StringBuilder()
		sb.append("{")
		sb.append("\"userPofileName\":\"")
		sb.append(this.getUserProfileName().toString())
		sb.append("\"")
		sb.append(",")
		//
		sb.append("\"profileDirectoryName\":\"")
		sb.append(this.getProfileDirectoryName())
		sb.append("\"")
		sb.append(",")
		//
		sb.append("\"userDataDirector\":\"")
		sb.append(this.getUserDataDirectory().toString())
		sb.append("\"")
		sb.append("}")
		return JsonOutput.prettyPrint(sb.toString())
	}
}
