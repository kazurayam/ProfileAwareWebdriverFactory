package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.UserProfile
import org.junit.BeforeClass
import org.openqa.selenium.chrome.ChromeDriver

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.openqa.selenium.Cookie
import org.openqa.selenium.remote.DesiredCapabilities

import io.github.bonigarcia.wdm.WebDriverManager
import groovy.json.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author kazurayam
 */
@RunWith(JUnit4.class)
class ChromeDriverFactoryTest {

	static Path outputFolder

	@BeforeClass
	static void beforeClass() {
		WebDriverManager.chromedriver().setup()
		outputFolder = Paths.get(".").resolve("build/tmp/testOutput")
				.resolve(ChromeDriverFactoryTest.class.getSimpleName())
		Files.createDirectories(outputFolder)
	}

	@Before
	void setup() {}

	@Test
	void test_newChromeDriver_no_default_settings() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory(false)
		//
		ChromeDriver driver = cdFactory.newChromeDriver()
		assertNotNull(driver)
		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		String dcJson = cdFactory.getEmployedDesiredCapabilitiesAsJSON()
		println("DesiredCapabilities is\n${dcJson}")
		//
		driver.navigate().to('http://example.com/')
		driver.quit()
		//
		def jsonObject = new JsonSlurper().parseText(dcJson)
		/* in case "with default setting" you will see
		DesiredCapabilities is
		{
			"acceptSslCerts": true,
			"browserName": "chrome",
			"goog:chromeOptions": {
				"args": [
						"window-size=1024,768",
						...
		 */
		assertFalse("window-size option should not be there when no default setting",
				jsonObject["goog:chromeOptions"]["args"].contains("window-size=1024,768")
		)
	}

	/**
	 * Basic case.
	 * Instantiate a ChromeDriver to open a Chrome browser with the default profile.
	 * 
	 */
	@Test
	void test_newChromeDriver_noUserProfileSpecified() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()

		ChromeDriver driver = cdFactory.newChromeDriver()
		assertNotNull(driver)
		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		String dcJson = cdFactory.getEmployedDesiredCapabilitiesAsJSON()
		println("DesiredCapabilities is\n${dcJson}")
		//
		driver.navigate().to('http://example.com/')
		driver.quit()
	}

	@Test
	void test_enableChromeDriverLog() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		cdFactory.enableChromeDriverLog(outputFolder)
		ChromeDriver driver = cdFactory.newChromeDriver()
		assertNotNull(driver)
		Path logFile = outputFolder.resolve(ChromeDriverUtils.LOG_FILE_NAME)
		assertTrue(Files.exists(logFile))
		assertTrue(logFile.size() > 0)
	}

	/**
	 * Instantiate a ChromeDriver to open a Chrome browser specifying a user profile "Picaso"
	 * while cloning the User Data directory to a temporary folder
	 */
	@Test
	void test_newChromeDriver_byUserProfile_TOGO() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		ChromeDriver driver = cdFactory.newChromeDriver(new UserProfile('Picaso'))
		assertNotNull(driver)

		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		String dcJson = cdFactory.getEmployedDesiredCapabilitiesAsJSON()
		println("DesiredCapabilities is\n${dcJson}")

		println("ChromeDriver has been instantiated with profile Picaso")
		driver.navigate().to('http://example.com/')
		driver.quit()
	}

	@Test
	void test_newChromeDriver_byProfileDirectoryName_TO_GO() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		ChromeDriver driver = cdFactory.newChromeDriver(new ProfileDirectoryName('Default'))
		assertNotNull(driver)

		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		String dcJson = cdFactory.getEmployedDesiredCapabilitiesAsJSON()
		println("DesiredCapabilities is\n${dcJson}")

		println("ChromeDriver has been instantiated with profile directory Default")
		driver.navigate().to('http://example.com/')
		driver.quit()
	}



	// I ignore this as it tends to fail easily when a Chrome process is already in action
	@Ignore
	@Test
	void test_newChromeDriver_byProfileDirectoryName_FOR_HERE() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		ChromeDriver driver = cdFactory.newChromeDriver(new ProfileDirectoryName('Default'),
				ChromeDriverFactory.UserDataAccess.FOR_HERE)
		assertNotNull(driver)

		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		String dcJson = cdFactory.getEmployedDesiredCapabilitiesAsJSON()
		println("DesiredCapabilities is\n${dcJson}")

		println("ChromeDriver has been instantiated with profile directory Default")
		driver.navigate().to('http://example.com/')
		driver.quit()
	}


	/**
	 * Instantiate a ChromeDriver to open a Chrome browser specifying a user profile "Picaso"
	 * while cloning the User Data directory to a temporary folder
	 * You are likely to see an error:
	 * > invalid argument: user data directory is already in use, please specify a unique value for --user-data-dir argument, or don't use --user-data-dir
	 * when you have at least 1 Chrome browser already opened.
	 */
	@Ignore
	// This test case is ignored because it tends to fail easily:
	// when you have Chrome opened when you execute this test, it will certainly fail
	@Test
	void test_newChromeDriver_withUserProfile_FOR_HERE() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		ChromeDriver driver = cdFactory.newChromeDriver(
				new UserProfile('Picaso'),
				ChromeDriverFactory.UserDataAccess.FOR_HERE)
		assertNotNull(driver)

		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		String dcJson = cdFactory.getEmployedDesiredCapabilitiesAsJSON()
		println("DesiredCapabilities is\n${dcJson}")

		driver.navigate().to('http://example.com/')
		driver.quit()
	}

	/**
	 * open a session using a user profile Picaso and navigate too http://127.0.0.1, then close the session.
	 * the session will create a cookie "timestamp".
	 * open a second session using Picaso Profile again.
	 * I expect the second session will use the same value of timestamp cookie. So I test it.
	 * 
	 */
	@Ignore
	@Test
	public void test_if_cookie_is_retained_in_profile_accross_2_sessions() {
		// we want Headless
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		//ChromeOptionsModifier com = new ChromeOptionsModifierHeadless()
		//cdFactory.addChromeOptionsModifier(com)
		//
		String url = 'http://localhost/'
		// 1st session
		ChromeDriver driver = cdFactory.newChromeDriver(new UserProfile('Picaso'))
		driver.navigate().to(url)
		Set<Cookie> cookies = driver.manage().getCookies()
		println "1st session: " + cookies
		String phpsessid1st = driver.manage().getCookieNamed('timestamp')
		driver.quit()

		// 2nd session
		driver = cdFactory.newChromeDriver(new UserProfile('Picaso'))
		driver.navigate().to(url)
		cookies = driver.manage().getCookies()
		println "2nd session: " + cookies
		String phpsessid2nd = driver.manage().getCookieNamed('timestamp')
		driver.quit()
		//
		assert phpsessid1st == phpsessid2nd;
	}

	@Test
	void test_addChromeOptionsModifier_incognito() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		//
		cdFactory.addChromeOptionsModifier(ChromeOptionsModifiers.incognito())
		//
		ChromeDriver driver = cdFactory.newChromeDriver()
		assertNotNull(driver)
		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		String dcJson = cdFactory.getEmployedDesiredCapabilitiesAsJSON()
		println("DesiredCapabilities is\n${dcJson}")
		driver.navigate().to('http://example.com/')
		driver.quit()
		//
		def jo = new JsonSlurper().parseText(dcJson)
		/*
		DesiredCapabilities is
		{
			"acceptSslCerts": true,
			"browserName": "chrome",
			"goog:chromeOptions": {
				"args": [
						"--incognito",
						...
		 */
		assertTrue(jo["goog:chromeOptions"]["args"].contains("--incognito"))
	}

	@Test
	void test_ChromeDriver_metadata_empty() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		ChromeDriver driver = cdFactory.newChromeDriver()
		assertEquals(Optional.empty(), driver.userProfile)
		assertEquals(Optional.empty(), driver.userDataAccess)
	}

	@Test
	void test_ChromeDriver_metadata_stuffed() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		ChromeDriver driver = cdFactory.newChromeDriver(new ProfileDirectoryName('Default'))
		assertNotNull(driver)
		assertTrue(driver.userProfile.isPresent())
		assertTrue(driver.userDataAccess.isPresent())
		driver.userProfile.ifPresent({ ChromeUserProfile up ->
			println up
			assertEquals(new UserProfile("Kazuaki"), up.getUserProfile())
			assertEquals(new ProfileDirectoryName("Default"), up.getProfileDirectoryName())
			assertTrue(Files.exists(up.getUserDataDir()))
			// e.g, "/Users/kazurayam/Library/Application Support/Google/Chrome"
		})
	}

}
