package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.CacheDirectoryName
import com.kazurayam.webdriverfactory.UserProfile

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
		Path userDataDir= ChromeProfileUtils.getDefaultUserDataDir()
		// then:
		assertNotNull(userDataDir)
		assertTrue(Files.exists(userDataDir))
	}

	@Test
	void test_getUserProfileList() {
		List<ChromeUserProfile> userProfiles = ChromeProfileUtils.getChromeUserProfileList()
		assertTrue(userProfiles.size() > 0)
	}

	@Test
	void test_listAllUserProfiles() {
		String text = ChromeProfileUtils.allChromeUserProfilesAsString()
		//println text
		assertTrue( text.length() > 0)
	}

	/**
	 * This test requires you to have a custom profile 'Picasso' defined in your Google Chrome browser
	 */
	@Test
	void test_findChromeUserProfile() {
		ChromeUserProfile userProfile = ChromeProfileUtils.findChromeUserProfile(new UserProfile('Picasso'))
		assertNotNull("ChromeProfileUtils.findChromeUserProfile() returned null", userProfile)
		assertEquals(userProfile.getUserProfile(), new UserProfile('Picasso'))
	}

	@Test
	void test_getChromeProfileNameByDirectoryName() {
		String profileName =
				ChromeProfileUtils.findUserProfileByCacheDirectoryName(
						new CacheDirectoryName('Default'))
		assertNotNull(profileName)
		//println("DirectoryName \'Default\' is associated with the Profile \'${profileName}\'")
	}
}