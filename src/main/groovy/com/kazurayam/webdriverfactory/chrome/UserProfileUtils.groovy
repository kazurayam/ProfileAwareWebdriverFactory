package com.kazurayam.webdriverfactory.chrome

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

import com.kazurayam.webdriverfactory.utils.OSIdentifier

final class UserProfileUtils {

	private UserProfileUtils() {}

	/*
	 *
	 */
	static List<UserProfile> getUserProfiles() {
		List<UserProfile> userProfiles = new ArrayList<UserProfile>()
		Path userDataDirectory = getUserDataDirectory()
		List<Path> dirStream = Files.list(userDataDirectory).collect(Collectors.toList());
		for (Path dir : dirStream) {
			if (Files.exists(dir.resolve('Preferences'))) {
				UserProfile cp = new UserProfile(dir)
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
	static UserProfile getUserProfile(String name) {
		List<UserProfile> userProfiles = this.getUserProfiles()
		for (UserProfile userProfile: userProfiles) {
			//System.out.println("[ChromeProfileFinder#getUserProfile] userProfile.getName()==${userProfile.getName()}, userProfile.getDirectoryName()=${userProfile.getDirectoryName()}")
			if (userProfile.getName() == name) {
				return userProfile
			}
		}
		return null
	}

	static UserProfile getUserProfileDefault() {
		return getUserProfileByDirectoryName("Default")
	}

	/**
	 * if a Profile of the name is defined, return true, otherwise false
	 *
	 * @param name
	 * @return
	 */
	static boolean hasUserProfile(String name) {
		return getUserProfile(name) != null
	}

	static UserProfile getUserProfileByDirectoryName(String profileDirectoryName) {
		List<UserProfile> userProfiles = getUserProfiles()
		for (UserProfile userProfile : userProfiles ) {
			if (userProfile.getDirectoryName() == profileDirectoryName) {
				return userProfile
			}
		}
	}

	static String getUserProfileNameByDirectoryName(String profileDirectoryName) {
		return getUserProfileByDirectoryName(profileDirectoryName).getName()
	}

	static String listUserProfiles() {
		List<UserProfile> userProfiles = getUserProfiles()
		Collections.sort(userProfiles)
		StringBuilder sb = new StringBuilder()
		sb.append(String.format("%-15s","user name"))
		sb.append("\t|")
		sb.append("profile path")
		sb.append("\n")
		sb.append(('-' * 15) + '\t|' + ('-' * 15) + "\n")
		for (UserProfile userProfile : userProfiles) {
			sb.append(String.format("%-15s",userProfile.getName()))
			sb.append("\t|")
			sb.append(userProfile.getProfilePath().getFileName().toString())
			sb.append("\n")
		}
		return sb.toString()
	}


	/**
	 *
	 * @return
	 */
	static Path getUserDataDirectory() {
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

    /**
     * @returns the path of directory in which Chrome Profile of 'name' is located
     */
    static Path getChromeProfileDirectory(String name) {
        Objects.requireNonNull(name, "name must not be null")
        UserProfile cProfile = UserProfileUtils.getUserProfile(name)
        if (cProfile != null) {
            return cProfile.getProfilePath()
        } else {
            return null
        }
    }
}
