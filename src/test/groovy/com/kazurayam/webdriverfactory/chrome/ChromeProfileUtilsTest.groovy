package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.ProfileDirectoryName

import java.nio.file.Files
import java.nio.file.Path

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4.class)
class ChromeProfileUtilsTest {

	@Test
	void test_findUserDataDir() {
		// when:
		Path userDataDir= ChromeUserProfileUtils.getDefaultUserDataDir()
		// then:
		assertNotNull(userDataDir)
		assertTrue(Files.exists(userDataDir))
	}

	@Test
	void test_getUserProfiles() {
		List<ChromeUserProfile> userProfiles = ChromeUserProfileUtils.getChromeUserProfileList()
		assertTrue(userProfiles.size() > 0)
	}

	@Test
	void test_listAllUserProfiles() {
		String text = ChromeUserProfileUtils.allChromeUserProfilesAsString()
		//println text
		assertTrue( text.length() > 0)
	}

	/**
	 * This test requires you to have a custom profile 'Picasso' defined in your Google Chrome browser
	 */
	@Test
	void test_getUserProfile() {
		ChromeUserProfile userProfile = ChromeUserProfileUtils.findChromeUserProfile(new ChromeUserProfile('Picasso'))
		assertNotNull(userProfile)
		assertEquals(userProfile.getUserProfile(), new ChromeUserProfile('Picasso'))
	}

	@Test
	void test_getChromeProfileNameByDirectoryName() {
		String profileName =
				ChromeUserProfileUtils.findUserProfileByProfileDirectoryName(
						new ProfileDirectoryName('Default'))
		assertNotNull(profileName)
		//println("DirectoryName \'Default\' is associated with the Profile \'${profileName}\'")
	}
}