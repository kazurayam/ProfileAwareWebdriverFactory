package com.kazurayam.webdriverfactory.chrome

import java.nio.file.Path

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * @author kazurayam
 *
 */
@RunWith(JUnit4.class)
class UserProfileUtilsTest {

	@Test
	void test_getChromeUserDataDirectory() {
		// when:
		Path profileDirectory = UserProfileUtils.getUserDataDirectory().resolve('Default')
		UserProfile defaultProfile = new UserProfile(profileDirectory)
		// then:
		assertNotNull(defaultProfile)
		assertNotNull(defaultProfile.getName())
		assertEquals(defaultProfile.getDirectoryName(),'Default')
		assertEquals(defaultProfile.getProfilePath(), profileDirectory)
	}

	@Test
	void test_getChromeProfiles() {
		List<UserProfile> chromeProfiles = UserProfileUtils.getUserProfiles()
		assertTrue(chromeProfiles.size() > 0)
	}

	@Test
	void test_listChromeProfiles() {
		String text = UserProfileUtils.listUserProfiles()
		assertTrue( text.length() > 0)
	}

	/**
	 * This test requires you to have a custom profile 'Katalon' defined in your Google Chrome browser
	 */
	@Test
	void test_getChromeProfile() {
		UserProfile userProfile = UserProfileUtils.getUserProfile('Katalon')
		assertNotNull(userProfile)
		assertEquals(userProfile.getName(), 'Katalon')
	}

	@Test
	void test_getChromeProfileNameByDirectoryName() {
		String profileName = UserProfileUtils.getUserProfileNameByDirectoryName('Default')
		assertNotNull(profileName)
		println("DirectoryName \'Default\' is associated with the Profile \'${profileName}\'")
	}
}