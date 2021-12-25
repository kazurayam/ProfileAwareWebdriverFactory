package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.UserProfile

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

import com.kazurayam.webdriverfactory.utils.OSIdentifier

final class ChromeProfileUtils {

	static Path findUserDataDirectory() {
		if (OSIdentifier.isWindows()) {
			// It is important that this chromeProfilesPath ends with User Data and not with the profile folder
			// %HOME%\AppData\Local\Google\Chrome\User Data
			return Paths.get('C:', 'Users', System.getProperty('user.name'),
					'AppData', 'Local', 'Google', 'Chrome', 'User Data')
		} else if (OSIdentifier.isMac()) {
			// ~/Library/Application Support/Google/Chrome
			return Paths.get(System.getProperty('user.home')).resolve('Library').
					resolve('Application Support').resolve('Google').resolve('Chrome')
		} else if (OSIdentifier.isUnix()) {
			// ~/.config/google-chrome
			return Paths.get(System.getProperty('user.home')).resolve('.config').
					resolve('google-chrome')
		} else {
			throw new IllegalStateException(
					"Windows, Mac, Linux are supported. Other platforms are not supported.")
		}
	}

	/*
	 *
	 */
	static List<ChromeUserProfile> getChromeUserProfileList() {
		List<ChromeUserProfile> userProfiles = new ArrayList<ChromeUserProfile>()
		Path userDataDirectory = findUserDataDirectory()
		List<Path> dirs = Files.list(userDataDirectory).collect(Collectors.toList());
		for (Path dir : dirs) {
			if (Files.exists(dir.resolve('Preferences'))) {
				ChromeUserProfile cp = new ChromeUserProfile(
						userDataDirectory, dir.getFileName().toString())
				userProfiles.add(cp)
			}
		}
		return userProfiles
	}

	/**
	 *
	 * @param name name of a Chrome Profile. e.g, "Russ Thomas"
	 * @return
	 */
	static ChromeUserProfile findChromeUserProfile(UserProfile userProfileName) {
		Objects.requireNonNull(userProfileName)
		List<ChromeUserProfile> userProfiles = this.getChromeUserProfileList()
		for (ChromeUserProfile userProfile: userProfiles) {
			//System.out.println("[ChromeProfileFinder#getUserProfile] userProfile.getName()==${userProfile.getName()}, userProfile.getDirectoryName()=${userProfile.getDirectoryName()}")
			if (userProfile.getUserProfileName() == userProfileName) {
				return userProfile
			}
		}
		return null
	}

	/**
	 * if a Profile of the name is defined, return true, otherwise false
	 *
	 * @param name
	 * @return
	 */
	static boolean hasChromeUserProfile(UserProfile userProfileName) {
		return findChromeUserProfile(userProfileName) != null
	}

	static ChromeUserProfile findChromeUserProfileByProfileDirectoryName(
			ProfileDirectoryName profileDirectoryName) {
		List<ChromeUserProfile> chromeUserProfiles = getChromeUserProfileList()
		for (ChromeUserProfile chromeUserProfile : chromeUserProfiles ) {
			if (chromeUserProfile.getProfileDirectoryName() == profileDirectoryName) {
				return chromeUserProfile
			}
		}
	}

	static UserProfile findUserProfileByProfileDirectoryName(ProfileDirectoryName profileDirectoryName) {
		return findChromeUserProfileByProfileDirectoryName(profileDirectoryName).getUserProfileName()
	}

	static String allChromeUserProfilesAsString() {
		List<ChromeUserProfile> userProfiles = getChromeUserProfileList()
		Collections.sort(userProfiles)
		StringBuilder sb = new StringBuilder()
		sb.append("[")
		int count = 0
		for (ChromeUserProfile up in userProfiles) {
			if (count > 0) {
				sb.append(",")
			}
			sb.append(up.toString())
			count += 1
		}
		sb.append("]")
		return sb.toString()
	}

	private ChromeProfileUtils() {}

}
