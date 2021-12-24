package com.kazurayam.webdriverfactory.chrome


import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * @author kazurayam
 *
 */
@RunWith(JUnit4.class)
public class ChromeProfileFinderTest {

	@Test
	void test_getChromeProfiles() {
		List<ChromeProfile> chromeProfiles = ChromeProfileFinder.getChromeProfiles()
		assertTrue(chromeProfiles.size() > 0)
	}

	@Test
	void test_listChromeProfiles() {
		String text = ChromeProfileFinder.listChromeProfiles()
		assertTrue( text.length() > 0)
	}

	/**
	 * This test requires you to have a custom profile 'Katalon' defined in your Google Chrome browser
	 */
	@Test
	void test_getChromeProfile() {
		ChromeProfile cp = ChromeProfileFinder.getChromeProfile('Katalon')
		assertThat(cp, is(notNullValue()))
		assertThat(cp.getName(), is('Katalon'))
	}

	@Test
	void test_getChromeProfileNameByDirectoryName() {
		String profileName = ChromeProfileFinder.getChromeProfileNameByDirectoryName('Default')
		assertThat(profileName, is(notNullValue()))
		println("DirectoryName \'Default\' is associated with the Profile \'${profileName}\'")
	}
}