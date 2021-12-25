package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifier

import java.nio.file.Files
import java.nio.file.Path

import org.openqa.selenium.chrome.ChromeOptions

class ChromeOptionsModifierUserProfile implements ChromeOptionsModifier {

	private Path userDataDirectory_
	private Path profileDirectory_

	ChromeOptionsModifierUserProfile(Path userDataDirectory, Path profileDirectory) {
		Objects.requireNonNull(userDataDirectory, "userDataDirectory must not be null")
		if (!Files.exists(userDataDirectory)) {
			throw new IllegalArgumentException("${userDataDirectory} does not exist")
		}
		Objects.requireNonNull(profileDirectory, "profileDirectory must not be null")
		if (!Files.exists(profileDirectory)) {
			throw new IllegalArgumentException("${profileDirectory} does not exist")
		}
		this.userDataDirectory_ = userDataDirectory 
		this.profileDirectory_ = profileDirectory
	}

	ChromeOptions modify(ChromeOptions chromeOptions) {
		Objects.requireNonNull(chromeOptions, "chromeOptions must not be null")
		chromeOptions.addArguments("user-data-dir=" + userDataDirectory_.toString())
		chromeOptions.addArguments("profile-directory=${profileDirectory_.getFileName().toString()}")
		return chromeOptions
	}
}
