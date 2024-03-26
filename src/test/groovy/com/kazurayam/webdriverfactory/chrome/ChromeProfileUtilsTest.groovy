package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.CacheDirectoryName
import com.kazurayam.webdriverfactory.UserProfile
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Files
import java.nio.file.Path

import static org.junit.Assert.*

@RunWith(JUnit4.class)
class ChromeProfileUtilsTest {

	Logger logger = LoggerFactory.getLogger(ChromeProfileUtilsTest.class);

	@Test
	void test_getDefaultUserDataDir() {
		// when:
		Path userDataDir= ChromeProfileUtils.getDefaultUserDataDir()
		// then:
		assertNotNull(userDataDir)
		assertTrue(Files.exists(userDataDir))
	}

	@Test
	void test_getChromeUserProfileList() {
		List<ChromeUserProfile> userProfiles = ChromeProfileUtils.getChromeUserProfileList()
		//printListOfChromeUserProfile(userProfiles)
		assertTrue(userProfiles.size() > 0)
	}

	private void printListOfChromeUserProfile(List<ChromeUserProfile> list) {
		logger.info("[test_getChromeUserProfileList] userProfiles: \n" +
				ChromeProfileUtils.stringifyListOfChromeUserProfile(list));
	}

	@Test
	void test_allChromeUserProfileAsString() {
		String text = ChromeProfileUtils.allChromeUserProfilesAsString()
		logger.info("[test_allChromeUserProfileAsString] text: " + text)
		assertTrue( text.length() > 0)
	}

	/**
	 * This test requires you to have a custom profile 'Picasso' defined in
	 * your Google Chrome browser
	 */
	@Test
	void test_findChromeUserProfile() {
		ChromeUserProfile userProfile =
				ChromeProfileUtils.findChromeUserProfile(new UserProfile('Picasso'))
		assertNotNull("findChromeUserProfile() returned null", userProfile)
		assertEquals(userProfile.getUserProfile(), new UserProfile('Picasso'))
	}

	@Test
	void test_findUserProfileByCacheDirectoryName() {
		String profileName =
				ChromeProfileUtils.findUserProfileByCacheDirectoryName(
						new CacheDirectoryName('Default'))
		assertNotNull(profileName)
	}
}