package com.kazurayam.webdriverfactory.firefox

import com.kazurayam.webdriverfactory.UserProfile
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import com.kazurayam.subprocessj.Subprocess
import com.kazurayam.subprocessj.Subprocess.CompletedProcess

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*

import org.junit.Ignore
import org.junit.Test
import org.openqa.selenium.WebDriver

import io.github.bonigarcia.wdm.WebDriverManager

class FirefoxDriverFactoryTest {

	LaunchedFirefoxDriver launched

	@BeforeClass
	static void beforeClass() {
		// GitHub Personal Access Token
		Subprocess subprocess = new Subprocess()
		CompletedProcess cp = subprocess.run(Arrays.asList(
				"security", "find-internet-password",
				"-s", "github.com", "-a", "kazurayam",
				"-w"))
		assert cp.returncode() == 0
		String GHT = cp.stdout().get(0).trim()
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
		firefoxUserProfile.ifPresent({it ->
			println("firefoxUserProfile => ${it}")
		})
		//
		Optional<FirefoxDriverFactory.UserDataAccess> instruction = launched.getInstruction()
		assert instruction.isPresent()
		instruction.ifPresent({ it ->
			println("instruction => ${it}")
		})
		//
		Optional<FirefoxOptions> options = launched.getEmployedOptions()
		assert options.isPresent()
		launched.getEmployedOptionsAsJSON().ifPresent({it ->
			println("options => ${it}")
		})
	}
}
