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
class ChromeDriverUtilsTest {

	@Test
	void test_getChromeProfileDirectory() {
		// when:
		Path profileDirectory = UserProfileUtils.getChromeProfileDirectory('Katalon')
        UserProfile katalonProfile = new UserProfile(profileDirectory)
		// then:
		assertNotNull(katalonProfile)
		assertNotNull(katalonProfile.getName())
		//assertEquals(katalonProfile.getDirectoryName(), 'Profile 22')    // dependents on the runtime environment
		assertEquals(katalonProfile.getProfilePath(), profileDirectory)
	}
	

}