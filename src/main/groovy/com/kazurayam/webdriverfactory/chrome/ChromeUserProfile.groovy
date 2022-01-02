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

	private final Path userDataDir
	private final String profileDirectoryName

	private UserProfile userProfileName
	private String preferences

	/**
	 * @param userDataDir "~/Library/Application Support/Google/Chrome/"
	 * @param profileName "Default", "Profile 1", "Profile 2", "Profile 3", ...
	 */
    ChromeUserProfile(Path userDataDir, String profileDirectoryName) {
		Objects.requireNonNull(userDataDir)
		Objects.requireNonNull(profileDirectoryName)
		if (! Files.exists(userDataDir)) {
			throw new IllegalArgumentException("${userDataDir} is not found")
		}
		Path profilePath = userDataDir.resolve(profileDirectoryName)
		if (! Files.exists(profilePath)) {
			throw new IllegalArgumentException("${p} is not found")
		}
		this.userDataDir = userDataDir
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

	Path getUserDataDir() {
		return this.userDataDir
	}

	ProfileDirectoryName getProfileDirectoryName() {
		return new ProfileDirectoryName(this.profileDirectoryName)
	}

	Path getChromeUserProfileDirectory() {
		return this.getUserDataDir().resolve(this.getProfileDirectoryName().getName())
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
		return this.getUserDataDir() == other.getUserDataDir() &&
				this.getProfileDirectoryName() == other.getProfileDirectoryName()
	}

	@Override
	int hashCode() {
		int hash = 7
		hash = 31 * hash + (int) this.getUserDataDir().hashCode()
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
		sb.append(this.getUserDataDir().toString())
		sb.append("\"")
		sb.append("}")
		return JsonOutput.prettyPrint(sb.toString())
	}
}
