package com.kazurayam.webdriverfactory.firefox

import com.kazurayam.webdriverfactory.TestUtils
import com.kazurayam.webdriverfactory.UserProfile
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions

import java.nio.file.Path

import static org.junit.Assert.*

import org.junit.Ignore
import org.junit.Test

import io.github.bonigarcia.wdm.WebDriverManager

@Ignore
class FirefoxDriverFactoryTest {

	LaunchedFirefoxDriver launched

	@BeforeClass
	static void beforeClass() {
		String GHT = TestUtils.getGitHubPersonalAccessToken()
		WebDriverManager.firefoxdriver().gitHubToken(GHT).setup()
	}

	@Before
	void setup() {
		launched = null
	}

	@After
	void teardown() {
		if (launched != null) {
			if (launched.getDriver() != null) {
				launched.getDriver().quit()
			}
			launched = null
		}
	}

	@Test
	void test_newFirefoxDriver_noArg() {
		FirefoxDriverFactory factory = FirefoxDriverFactory.newFirefoxDriverFactory()
		launched = factory.newFirefoxDriver()
		assert launched != null
		launched.getDriver().navigate().to("http://example.com")
		Thread.sleep(1000)
	}

	/**
	 * Instantiate a FirefoxDriver to open a Firefox browser specifying a user profile "Picasso"
	 *
	 */
	@Test
	void test_newFirefoxDriverWithProfile() {
		FirefoxDriverFactory factory = FirefoxDriverFactory.newFirefoxDriverFactory()
		launched = factory.newFirefoxDriver(new UserProfile("Picasso"))
		assert launched != null
		//
		launched.getDriver().navigate().to("http://example.com")
		Thread.sleep(1000)
		//
		Optional<FirefoxUserProfile> firefoxUserProfile = launched.getFirefoxUserProfile()
		assert firefoxUserProfile.isPresent()
		//
		Optional<FirefoxOptions> options = launched.getEmployedOptions()
		assert options.isPresent()
	}
}
