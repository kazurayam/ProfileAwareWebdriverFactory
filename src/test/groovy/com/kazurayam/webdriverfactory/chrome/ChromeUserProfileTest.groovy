package com.kazurayam.webdriverfactory.chrome

import groovy.json.JsonSlurper

import static org.junit.Assert.*

import java.nio.file.Path

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * @author kazurayam
 *
 */
@RunWith(JUnit4.class)
class ChromeUserProfileTest {

	@Test
	void test_ChromeProfile() {
		// when:
		Path userDataDirectory = ChromeProfileUtils.getDefaultUserDataDirectory()
        ChromeUserProfile defaultProfile =
				new ChromeUserProfile(userDataDirectory, 'Default')
		// then:
		assertNotNull(defaultProfile)
		assertNotNull(defaultProfile.getUserProfileName())
		assertTrue(defaultProfile.getUserProfileName().toString().startsWith('K'))  // kazurayam's name
		assertEquals(userDataDirectory, defaultProfile.getUserDataDirectory())
	}

	@Test
	void test_getUserProfileDirectory() {
		Path userDataDirectory = ChromeProfileUtils.getDefaultUserDataDirectory()
		ChromeUserProfile defaultProfile =
				new ChromeUserProfile(userDataDirectory, 'Default')
		Path userProfileDirectory = defaultProfile.getChromeUserProfileDirectory()
		assertNotNull(userProfileDirectory)
		assertEquals('Default',
				userProfileDirectory.getFileName().toString())
	}

	@Test
	void test_toString() {
		Path userDataDirectory = ChromeProfileUtils.getDefaultUserDataDirectory()
		ChromeUserProfile defaultProfile =
				new ChromeUserProfile(userDataDirectory, 'Default')
		println defaultProfile.toString()
		JsonSlurper slurper = new JsonSlurper()
		def obj = slurper.parseText(defaultProfile.toString())
		assert obj != null
	}
}