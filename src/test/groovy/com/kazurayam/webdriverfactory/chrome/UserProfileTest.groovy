package com.kazurayam.webdriverfactory.chrome


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
class UserProfileTest {

	@Test
	void test_ChromeProfile() {
		// when:
		Path profileDirectory = UserProfileUtils.getUserDataDirectory().resolve('Default')
        UserProfile defaultProfile = new UserProfile(profileDirectory)
		// then:
		assertNotNull(defaultProfile)
		assertNotNull(defaultProfile.getName())
		assertEquals(defaultProfile.getDirectoryName(), 'Default')
		assertEquals(defaultProfile.getProfilePath(), profileDirectory)
	}
}