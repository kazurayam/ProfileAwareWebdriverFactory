package com.kazurayam.webdriverfactory.chrome


import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*

import java.nio.file.Files
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
	void test_getUserDataDirectory() {
		Path userDataDirectory = ChromeDriverUtils.getChromeUserDataDirectory()
		assertThat(Files.exists(userDataDirectory), is(true))
	}

	@Test
	void test_getChromeProfileDirectory() {
		// when:
		Path profileDirectory = ChromeDriverUtils.getChromeProfileDirectory('Katalon')
        ChromeProfile katalonProfile = new ChromeProfile(profileDirectory)
		// then:
		assertThat(katalonProfile, is(notNullValue()))
		assertThat(katalonProfile.getName(), is(notNullValue()))
		//assertThat(katalonProfile.getDirectoryName(), is('Profile 22'))    // dependent on the runtime environment; undetermined 
		assertThat(katalonProfile.getProfilePath(), is(profileDirectory))	
	}
	
	@Test
	void test_getChromeUserDataDirectory() {
		// when:
		Path profileDirectory = ChromeDriverUtils.getChromeUserDataDirectory().resolve('Default')
		ChromeProfile defaultProfile = new ChromeProfile(profileDirectory)
		// then:
		assertThat(defaultProfile, is(notNullValue()))
		assertThat(defaultProfile.getName(), is(notNullValue()))
		assertThat(defaultProfile.getDirectoryName(), is('Default'))
		assertThat(defaultProfile.getProfilePath(), is(profileDirectory))
	}
}