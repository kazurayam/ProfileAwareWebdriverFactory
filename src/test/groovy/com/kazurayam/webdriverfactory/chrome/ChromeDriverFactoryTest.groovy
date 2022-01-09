package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.timekeeper.Measurement
import com.kazurayam.timekeeper.Record
import com.kazurayam.timekeeper.Table
import com.kazurayam.timekeeper.Timekeeper
import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory.UserDataAccess
import com.kazurayam.webdriverfactory.desiredcapabilities.DesiredCapabilitiesModifier
import org.junit.After
import org.junit.BeforeClass
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

import java.security.MessageDigest
import java.time.LocalDateTime

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
	ChromeDriver driver

	@BeforeClass
	static void beforeClass() {
		WebDriverManager.chromedriver().setup()
		outputFolder = Paths.get(".").resolve("build/tmp/testOutput")
				.resolve(ChromeDriverFactoryTest.class.getSimpleName())
		Files.createDirectories(outputFolder)
	}

	@Before
	void setup() {
		driver = null
	}

	@After
	void tearDown() {
		if (driver != null) {
			driver.quit()
			driver = null
		}
	}

	@Test
	void test_newChromeDriver_no_default_settings() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory(false)
		driver = cdFactory.newChromeDriver()
		assertNotNull(driver)
		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		String dcJson = cdFactory.getEmployedDesiredCapabilitiesAsJSON()
		println("DesiredCapabilities is\n${dcJson}")
		//
		driver.navigate().to('http://example.com/')
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
		driver = cdFactory.newChromeDriver()
		assertNotNull(driver)
		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		String dcJson = cdFactory.getEmployedDesiredCapabilitiesAsJSON()
		println("DesiredCapabilities is\n${dcJson}")
		//
		driver.navigate().to('http://example.com/')
	}

	@Test
	void test_enableChromeDriverLog() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		cdFactory.enableChromeDriverLog(outputFolder)
		driver = cdFactory.newChromeDriver()
		assertNotNull(driver)
		Path logFile = outputFolder.resolve(ChromeDriverUtils.LOG_FILE_NAME)
		assertTrue(Files.exists(logFile))
		assertTrue(logFile.size() > 0)
	}

	/**
	 * Instantiate a ChromeDriver to open a Chrome browser specifying a user profile "Picasso"
	 * while cloning the User Data directory to a temporary folder
	 */
	@Test
	void test_newChromeDriver_byUserProfile_TOGO() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		driver = cdFactory.newChromeDriver(
				new UserProfile('Picasso'),
				UserDataAccess.TO_GO)
		assertNotNull(driver)

		// check if the user-data-dir/ProfileDireName/Cookie file is properly copied from the genuine one
		if (driver.userProfile.isPresent()) {
			driver.userProfile.ifPresent({ chromeUserProfile ->
				Path originalCookieFile = ChromeProfileUtils.getDefaultUserDataDir()
						.resolve(chromeUserProfile.getProfileDirectoryName().toString())
						.resolve("Cookies")
				Path clonedCookieFile = chromeUserProfile.getProfileDirectory().resolve("Cookies")
				boolean identical = filesAreIdentical(originalCookieFile, clonedCookieFile)
				assert identical: "${clonedCookieFile} (size=${clonedCookieFile.size()}) is not identical "+
						"to ${originalCookieFile} (size=${originalCookieFile.size()})"
			})
		} else {
			throw new IllegalStateException("driver.userProfile is empty")
		}

		//
		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		String dcJson = cdFactory.getEmployedDesiredCapabilitiesAsJSON()
		//println("DesiredCapabilities is\n${dcJson}")

		//println("ChromeDriver has been instantiated with profile Picasso")
		driver.navigate().to('http://example.com/')
	}

	@Test
	void test_newChromeDriver_byProfileDirectoryName_TO_GO() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		driver = cdFactory.newChromeDriver(
				new ProfileDirectoryName('Profile 1'),
				UserDataAccess.TO_GO)  // or 'Default'
		assertNotNull(driver)

		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		String dcJson = cdFactory.getEmployedDesiredCapabilitiesAsJSON()
		//println("DesiredCapabilities is\n${dcJson}")

		//println("ChromeDriver has been instantiated with profile directory Default")
		driver.navigate().to('http://example.com/')
	}



	// I ignore this as it tends to fail easily when a Chrome process is already in action
	@Ignore
	@Test
	void test_newChromeDriver_byProfileDirectoryName_FOR_HERE() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		driver = cdFactory.newChromeDriver(
				new ProfileDirectoryName('Profile 1'),
				UserDataAccess.FOR_HERE)
		assertNotNull(driver)

		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		String dcJson = cdFactory.getEmployedDesiredCapabilitiesAsJSON()
		//println("DesiredCapabilities is\n${dcJson}")

		//println("ChromeDriver has been instantiated with profile directory Default")
		driver.navigate().to('http://example.com/')
	}


	/**
	 * Instantiate a ChromeDriver to open a Chrome browser specifying a user profile "Picasso"
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
		driver = cdFactory.newChromeDriver(
				new UserProfile('Picasso'),
				UserDataAccess.FOR_HERE)
		assertNotNull(driver)

		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		String dcJson = cdFactory.getEmployedDesiredCapabilitiesAsJSON()
		//println("DesiredCapabilities is\n${dcJson}")

		driver.navigate().to('http://example.com/')
	}

	/**
	 * open a session using a user profile Picasso and navigate too http://127.0.0.1, then close the session.
	 * the session will create a cookie "timestamp".
	 * open a second session using Picasso Profile again.
	 * I expect the second session will use the same value of timestamp cookie. So I test it.
	 * 
	 */
	@Ignore
	@Test
	void test_if_cookie_is_retained_in_profile_accross_2_sessions() {
		// we want Headless
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		//ChromeOptionsModifier com = new ChromeOptionsModifierHeadless()
		//cdFactory.addChromeOptionsModifier(com)
		//
		String url = 'http://localhost/'
		// 1st session
		driver = cdFactory.newChromeDriver(new UserProfile('Picasso'))
		driver.navigate().to(url)
		Set<Cookie> cookies = driver.manage().getCookies()
		println "1st session: " + cookies
		String phpsessid1st = driver.manage().getCookieNamed('timestamp')
		driver.quit()

		// 2nd session
		driver = cdFactory.newChromeDriver(new UserProfile('Picasso'))
		driver.navigate().to(url)
		cookies = driver.manage().getCookies()
		println "2nd session: " + cookies
		String phpsessid2nd = driver.manage().getCookieNamed('timestamp')
		//
		assert phpsessid1st == phpsessid2nd
	}

	@Test
	void test_addChromeOptionsModifier_incognito() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		//
		cdFactory.addChromeOptionsModifier(ChromeOptionsModifiers.incognito())
		//
		driver = cdFactory.newChromeDriver()
		assertNotNull(driver)
		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		String dcJson = cdFactory.getEmployedDesiredCapabilitiesAsJSON()
		println("DesiredCapabilities is\n${dcJson}")
		driver.navigate().to('http://example.com/')
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
		driver = cdFactory.newChromeDriver()
		assertEquals(Optional.empty(), driver.userProfile)
		assertEquals(Optional.empty(), driver.userDataAccess)
	}

	@Test
	void test_ChromeDriver_metadata() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		driver = cdFactory.newChromeDriver(new ProfileDirectoryName('Default'))
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

	@Test
	void test_speed() {
		Timekeeper tk = new Timekeeper()
		Measurement m1 = new Measurement.Builder(
				"How long it took to open a Chrome browser",
				["Case"]).build()
		tk.add(new Table.Builder(m1).build())
		// open Chrome as default
		LocalDateTime before = LocalDateTime.now()
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		driver = cdFactory.newChromeDriver()
		LocalDateTime after = LocalDateTime.now()
		m1.recordDuration(["Case": "no specialization"], before, after)
		driver.quit()

		// open Chrome with ProfileDirectoryName
		before = LocalDateTime.now()
		cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		driver = cdFactory.newChromeDriver(new ProfileDirectoryName('Profile 1'))
		after = LocalDateTime.now()
		m1.recordDuration(["Case": "with ProfileDirectoryName"], before, after)
		// report
		Path md = outputFolder.resolve("test_speed.md")
		tk.report(md)
		println "Timekeeper report: ${md}"
		// fail if too slow to open Chrome window
		Record r = m1.getLast()
		float cap = 8.0
		assertTrue("r.getDurationMillis=${r.getDurationMillis() / 1000} should be less than ${cap}",
				r.getDurationMillis() / 1000 < cap)
	}

	@Test
	void test_waitForPageLoad_works() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
		cdFactory.pageLoadTimeout(10)
		driver = cdFactory.newChromeDriver()
		assertNotNull(driver)
		driver.navigate().to("http://example.com")
	}


	@Ignore
	@Test
	void test_applyChromeOptionsModifiers() {
		ChromeOptions chromeOptions = new ChromeOptions()
		List<ChromeOptionsModifier> modifiers = new ArrayList<>()
		modifiers.add(ChromeOptionsModifiers.withUserProfile(
				Paths.get("/Users/kazurayam/Library/Application Support/Google/Chrome/"),
				"Default"
		))
		modifiers.add(ChromeOptionsModifiers.withUserProfile(
				Paths.get("/private/var/folders/lh/jkh513dn7f3c0j09z131g1z00000gn/T/__user-data-dir__2083508313415793756/"),
				"Profile 6"
		))
		chromeOptions = ChromeDriverFactoryImpl.applyChromeOptionsModifiers(chromeOptions, modifiers)
		String s = chromeOptions.toString()
		// Capabilities {browserName: chrome, goog:chromeOptions: {args: [user-data-dir=/Users/kazura..., profile-directory=Default, user-data-dir=/private/var/..., profile-directory=Profile 6], extensions: []}}
		println s
		assert ! s.contains("profile-directory=Default")
	}

	@Test
	void test_filesAreIdentical() {
		Path file = Paths.get("build.gradle")
		assert filesAreIdentical(file, file)
	}

	private static boolean filesAreIdentical(Path file1, Path file2) {
		Objects.requireNonNull(file1)
		Objects.requireNonNull(file2)
		if ( ! Files.exists(file1)) {
			throw new IllegalArgumentException("${file1} is not present")
		}
		if ( ! Files.exists(file2)) {
			throw new IllegalArgumentException("${file2} is not present")
		}
		MessageDigest md = MessageDigest.getInstance("MD5")
		md.update(Files.readAllBytes(file1))
		byte[] digest1 = md.digest()
		md.update(Files.readAllBytes(file2))
		byte[] digest2 = md.digest();
		if (digest1.length == digest2.length) {
			boolean result = true
			for (int i = 0; i < digest1.length; i++) {
				if (digest1[i] != digest2[i]) {
					result = false
					break
				}
			}
			return result
		} else {
			return false
		}
	}
}
