package com.kazurayam.webdriverfactory.firefox

import com.kazurayam.webdriverfactory.TestUtils
import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.ChromeProfileUtils
import com.kazurayam.webdriverfactory.chrome.ChromeUserProfile
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import com.kazurayam.subprocessj.Subprocess
import com.kazurayam.subprocessj.Subprocess.CompletedProcess

import java.nio.file.Path

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
		/*
		Subprocess subprocess = new Subprocess()
		CompletedProcess cp = subprocess.run(Arrays.asList(
				"security", "find-internet-password",
				"-s", "github.com", "-a", "kazurayam",
				"-w"))
		assert cp.returncode() == 0
		*/
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

	/**
	 * Instantiate a ChromeDriver to open a Chrome browser specifying a user profile "Picasso"
	 * while cloning the User Data directory to a temporary folder
	 */
	@Test
	void test_if_cookie_file_is_cloned_TO_GO() {
		FirefoxDriverFactory factory = FirefoxDriverFactory.newFirefoxDriverFactory()
		launched = factory.newFirefoxDriver(
				new UserProfile('Picasso'),
				FirefoxDriverFactory.UserDataAccess.TO_GO)
		assertNotNull(launched)

		// check if the user-data-dir/ProfileDireName/cookie.sqlite file is properly copied
		// from the genuine one into the temporary directory
		launched.getFirefoxUserProfile().ifPresent({ FirefoxUserProfile firefoxUserProfile ->
			Path originalCookieFile = FirefoxProfileUtils.getDefaultUserDataDir()
					.resolve(firefoxUserProfile.getProfileDirectoryName().toString())
					.resolve("cookies.sqlite")
			Path clonedCookieFile = firefoxUserProfile.getProfileDirectory().resolve("cookies.sqlite")
			boolean identical = TestUtils.filesAreIdentical(originalCookieFile, clonedCookieFile)
			assert identical: "${clonedCookieFile} (size=${clonedCookieFile.size()}) is not identical "+
					"to ${originalCookieFile} (size=${originalCookieFile.size()})"
		})
		launched.getDriver().navigate().to('http://example.com/')
	}
}
