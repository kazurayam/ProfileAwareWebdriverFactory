package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.CacheDirectoryName
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
		LocalState localState = new LocalState(userDataDir, LocalState.LOCAL_STATE_FILENAME);
		String cacheName = localState.lookupCacheNameOf("Picasso")
		ChromeUserProfile cupPicasso =
				new ChromeUserProfile(userDataDir, new CacheDirectoryName(cacheName))

	}
}