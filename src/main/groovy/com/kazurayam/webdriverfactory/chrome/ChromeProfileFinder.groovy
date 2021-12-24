package com.kazurayam.webdriverfactory.chrome

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

import com.kazurayam.webdriverfactory.utils.OSIdentifier

final class ChromeProfileFinder {

	private ChromeProfileFinder() {}

	/*
	 *
	 */
	static List<ChromeProfile> getChromeProfiles() {
		List<ChromeProfile> chromeProfiles = new ArrayList<ChromeProfile>()
		Path userDataDirectory = ChromeDriverUtils.getChromeUserDataDirectory()
		List<Path> dirStream = Files.list(userDataDirectory).collect(Collectors.toList());
		for (Path dir : dirStream) {
			if (Files.exists(dir.resolve('Preferences'))) {
				ChromeProfile cp = new ChromeProfile(dir)
				chromeProfiles.add(cp)
			}
		}
		return chromeProfiles
	}

	/**
	 *
	 * @param name name of a Chrome Profile. e.g, "Russ Thomas"
	 * @return
	 */
	static ChromeProfile getChromeProfile(String name) {
		List<ChromeProfile> chromeProfiles = this.getChromeProfiles()
		for (ChromeProfile cProfile: chromeProfiles) {

			//System.out.println("[ChromeProfileFinder#getChromeProfile] cProfile.getName()==${cProfile.getName()}, cProfile.getDirectoryName()=${cProfile.getDirectoryName()}")

			if (cProfile.getName().equals(name)) {
				return cProfile
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
	static boolean hasChromeProfile(String profileName) {
		return getChromeProfile(profileName) != null
	}

	static ChromeProfile getChromeProfileByDirectoryName(Path profileDirectory) {
		return getChromeProfileByDirectoryName(profileDirectory.getFileName().toString())
	}

	static ChromeProfile getDefaultChromeProfile() {
		return getChromeProfileByDirectoryName("Default")
	}

	static ChromeProfile getChromeProfileByDirectoryName(String profileDirectoryName) {
		List<ChromeProfile> chromeProfiles = getChromeProfiles()
		for (ChromeProfile chromeProfile: chromeProfiles) {
			if (chromeProfile.getDirectoryName().equals(profileDirectoryName)) {
				return chromeProfile
			}
		}
	}

	static String getChromeProfileNameByDirectoryName(String profileDirectoryName) {
		return getChromeProfileByDirectoryName(profileDirectoryName).getName()
	}

	static String listChromeProfiles() {
		List<ChromeProfile> chromeProfiles = getChromeProfiles()
		Collections.sort(chromeProfiles)
		StringBuilder sb = new StringBuilder()
		sb.append(String.format("%-15s","user name"))
		sb.append("\t|")
		sb.append("profile path")
		sb.append("\n")
		sb.append(('-' * 15) + '\t|' + ('-' * 15) + "\n")
		for (ChromeProfile cp : chromeProfiles) {
			sb.append(String.format("%-15s",cp.getName()))
			sb.append("\t|")
			sb.append(cp.getProfilePath().getFileName().toString())
			sb.append("\n")
		}
		return sb.toString()
	}


	/**
	 *
	 * @return
	 */
	static Path getChromeUserDataDirectory() {
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
}
