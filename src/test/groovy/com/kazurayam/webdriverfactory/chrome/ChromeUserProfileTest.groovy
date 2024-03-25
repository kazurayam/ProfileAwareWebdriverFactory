package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.CacheDirectoryName
import com.kazurayam.webdriverfactory.UserProfile
import groovy.json.JsonSlurper
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Path

import static org.junit.Assert.*

/**
 * @author kazurayam
 *
 */
@RunWith(JUnit4.class)
class ChromeUserProfileTest {

	Logger logger = LoggerFactory.getLogger(ChromeUserProfileTest.class)

	@Test
	void test_ChromeProfile() {
		// when:
		Path userDataDir = ChromeProfileUtils.getDefaultUserDataDir()
        ChromeUserProfile defaultProfile =
				new ChromeUserProfile(userDataDir,
						new CacheDirectoryName('Default'))
		// then:
		assertNotNull(defaultProfile)
		assertNotNull(defaultProfile.getUserProfile())
		assertTrue(defaultProfile.getUserProfile().toString().startsWith('K'))  // kazurayam's name
		assertEquals(userDataDir, defaultProfile.getUserDataDir())
	}

	@Test
	void test_getUserProfileDirectory() {
		Path userDataDir = ChromeProfileUtils.getDefaultUserDataDir()
		ChromeUserProfile defaultProfile =
				new ChromeUserProfile(userDataDir,
						new CacheDirectoryName('Default')
				)
		Path userProfileDirectory = defaultProfile.getCacheDirectory()
		assertNotNull(userProfileDirectory)
		assertEquals('Default',
				userProfileDirectory.getFileName().toString())
	}

	@Test
	void test_toString() {
		Path userDataDir = ChromeProfileUtils.getDefaultUserDataDir()
		ChromeUserProfile defaultProfile =
				new ChromeUserProfile(userDataDir,
						new CacheDirectoryName('Default')
				)
		println defaultProfile.toString()
		assertTrue("defaultProfile.toString() should start with {",
				defaultProfile.toString().startsWith("{"));
		JsonSlurper slurper = new JsonSlurper()
		def obj = slurper.parseText(defaultProfile.toString())
		assert obj != null
	}

	@Test
	void test_getPreferences() {
		Path userDataDir = ChromeProfileUtils.getDefaultUserDataDir()
		Path localStateFile = userDataDir.resolve(LocalState.LOCAL_STATE_FILENAME)
		LocalState localState = new LocalState(localStateFile);
		Optional<String> cacheName = localState.lookupCacheNameOf("Picasso")
		ChromeUserProfile cupPicasso =
				new ChromeUserProfile(userDataDir,
						new CacheDirectoryName(cacheName.get()),
						new UserProfile("Picasso"))
		logger.info("[test_getPreferences] cupPicasso.getPreferences(): " + cupPicasso.getPreferences())
		assertNotNull(cupPicasso.getPreferences())
	}
}