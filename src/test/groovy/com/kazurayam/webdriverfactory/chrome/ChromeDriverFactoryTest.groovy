package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.timekeeper.Measurement
import com.kazurayam.timekeeper.Record
import com.kazurayam.timekeeper.Table
import com.kazurayam.timekeeper.Timekeeper
import com.kazurayam.webdriverfactory.CacheDirectoryName
import com.kazurayam.webdriverfactory.CookieServer
import com.kazurayam.webdriverfactory.TestUtils
import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory.UserDataAccess
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.openqa.selenium.Cookie

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime

import static org.junit.Assert.*

/**
 * @author kazurayam
 */
@RunWith(JUnit4.class)
class ChromeDriverFactoryTest {

	static Path outputFolder
	LaunchedChromeDriver launched
	static CookieServer cookieServer

	@BeforeClass
	static void beforeClass() {
		WebDriverManager.chromedriver()
				.clearDriverCache()
				.clearResolutionCache().setup()
		outputFolder = Paths.get(".").resolve("build/tmp/testOutput")
				.resolve(ChromeDriverFactoryTest.class.getSimpleName())
		Files.createDirectories(outputFolder)
		//
		cookieServer = new CookieServer()
		cookieServer.setBaseDir(Paths.get("./src/web"))
		cookieServer.setPrintRequestRequired(true)
		cookieServer.setDebugMode(true)
		cookieServer.startup()
	}

	@Before
	void setup() {
		launched = null
	}

	@After
	void tearDown() {
		if (launched != null) {
			launched.getDriver().quit()
			launched = null
		}
	}

	@AfterClass
	static void afterClass() {
		cookieServer.shutdown()
	}


	@Test
	void test_addChromeOptionsModifier_incognito() {
		ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		//
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.incognito())
		//
		launched = cdf.newChromeDriver()
		assertNotNull(launched)
		//println "raw: " + launched.getEmployedOptionsAsJSON()
		launched.getEmployedOptionsAsJSON().ifPresent({ json ->
			println("options is\n${json}")
		})
		launched.getDriver().navigate().to('http://example.com/')
		//
		launched.getEmployedOptionsAsJSON().ifPresent({ String json ->
			println "employed options as json: " + json
			/* Selenium 3
            {
    "browserName": "chrome",
    "goog:chromeOptions": {
        "args": [
            "disable-infobars",
            "disable-dev-shm-usage",
            "disableExtensions",
            "--no-sandbox",
            "disable-gpu",
            "window-size=1024,768",
            "--incognito"
        ],
        "extensions": [

        ],
        "prefs": {
            "plugins.plugins_disabled": [
                "Adobe Flash Player",
                "Chrome PDF Viewer"
            ],
            "profile.default_content_settings.popups": 0,
            "download.prompt_for_download": false,
            "download.default_directory": "/Users/kazuakiurayama/Downloads"
        }
    }
}
             */
			/* Selenium 4
{
  "args": [
    "plugins.plugins_disabled\u003d[Adobe Flash Player, Chrome PDF Viewer]",
    "profile.default_content_settings.popups\u003d0",
    "download.prompt_for_download\u003dfalse",
    "download.default_directory\u003d/Users/kazuakiurayama/Downloads",
    "disable-infobars",
    "disable-dev-shm-usage",
    "--no-sandbox",
    "disable-gpu",
    "window-size\u003d1024,768",
    "disableExtensions",
    "--incognito"
  ],
  "extensionFiles": [],
  "extensions": [],
  "experimentalOptions": {},
  "androidOptions": {},
  "capabilityName": "goog:chromeOptions",
  "caps": {
    "browserName": "chrome"
  }
}
			 */
			assertTrue("json does not contain --incognito", json.contains("--incognito"))
		})
		launched.getDriver().quit()
	}

	/**
	 * I would put @Ignore because this test will certainly fail.
	 * FOR_HERE causes failure.
	 */
	@Ignore
	@Test
	void test_ChromeDriver_metadata_FOR_HERE() {
		ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		launched = cdf.newChromeDriver(new CacheDirectoryName('Default'),
				UserDataAccess.FOR_HERE)
		assertNotNull(launched)
		assertTrue(launched.getChromeUserProfile().isPresent())
		assertTrue(launched.getInstruction().isPresent())
		launched.getChromeUserProfile().ifPresent({ ChromeUserProfile up ->
			println up
			assertEquals(new UserProfile("Kazuaki"), up.getUserProfile())
			assertEquals(new CacheDirectoryName("Default"), up.getCacheDirectoryName())
			assertTrue(Files.exists(up.getUserDataDir()))
			// e.g, "/Users/kazurayam/Library/Application Support/Google/Chrome"
		})
		launched.getDriver().quit()
	}

	@Test
	void test_ChromeDriver_metadata_empty() {
		ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		launched = cdf.newChromeDriver()
		assertEquals(Optional.empty(), launched.getChromeUserProfile())
		assertEquals(Optional.empty(), launched.getInstruction())
		launched.getDriver().quit()
	}

	@Test
	void test_enableChromeDriverLog() {
		ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		cdf.enableChromeDriverLog(outputFolder)
		launched = cdf.newChromeDriver()
		assertNotNull(launched)
		Path logFile = outputFolder.resolve(ChromeDriverUtils.LOG_FILE_NAME)
		assertTrue(Files.exists(logFile))
		assertTrue(logFile.size() > 0)
		launched.getDriver().quit()
	}


	/**
	 * Instantiate a ChromeDriver to open a Chrome browser specifying a user profile "Picasso"
	 * while cloning the User Data directory to a temporary folder
	 */
	@Test
	void test_if_cookie_file_is_cloned_TO_GO() {
		ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		launched = cdf.newChromeDriver(
				new UserProfile('Picasso'),
				UserDataAccess.TO_GO)
		assertNotNull(launched)

		// check if the user-data-dir/ProfileDireName/Cookie file is properly copied
		// from the genuine one into the temporary directory
		launched.getChromeUserProfile().ifPresent({ ChromeUserProfile chromeUserProfile ->
			Path originalCookieFile = ChromeProfileUtils.getDefaultUserDataDir()
					.resolve(chromeUserProfile.getCacheDirectoryName().toString())
					.resolve("Cookies")
			Path clonedCookieFile = chromeUserProfile.getCacheDirectory().resolve("Cookies")
			assert clonedCookieFile != null
			//assert originalCookieFile.size() == clonedCookieFile.size()
			//
			boolean identical = TestUtils.filesAreIdentical(originalCookieFile, clonedCookieFile)
			// the following line always fails.
			// perhaps the Cookie file is slightly updated as soon as the Chrome is launched.
			//assert identical: "${clonedCookieFile} (size=${clonedCookieFile.size()}) is not identical "+
			//		"to ${originalCookieFile} (size=${originalCookieFile.size()})"
		})

		//println("ChromeDriver has been instantiated with profile Picasso")
		launched.getDriver().navigate().to('http://example.com/')
		launched.getDriver().quit()
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
		ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		//ChromeOptionsModifier com = new ChromeOptionsModifierHeadless()
		//cdf.addChromeOptionsModifier(com)
		//
		String url = 'http://localhost/'
		// 1st session
		launched = cdf.newChromeDriver(new UserProfile('Picasso'))
		launched.getDriver().navigate().to(url)
		Set<Cookie> cookies = launched.getDriver().manage().getCookies()
		println "1st session: " + cookies
		String phpsessid1st = launched.getDriver().manage().getCookieNamed('timestamp')
		launched.getDriver().quit()

		// 2nd session
		launched = cdf.newChromeDriver(new UserProfile('Picasso'))
		launched.getDriver().navigate().to(url)
		cookies = launched.getDriver().manage().getCookies()
		println "2nd session: " + cookies
		String phpsessid2nd = launched.getDriver().manage().getCookieNamed('timestamp')
		launched.getDriver().quit()
		//
		assert phpsessid1st == phpsessid2nd
	}

	// I ignore this as it tends to fail easily when a Chrome process is already in action
	@Ignore
	@Test
	void test_newChromeDriver_byCacheDirectoryName_FOR_HERE() {
		ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		launched = cdf.newChromeDriver(
				new CacheDirectoryName('Profile 17'),
				UserDataAccess.FOR_HERE)
		assertNotNull(launched)

		//println("ChromeDriver has been instantiated with profile directory Default")
		launched.getDriver().navigate().to('http://example.com/')
		launched.getDriver().quit()
	}

	@Test
	void test_newChromeDriver_byCacheDirectoryName_TO_GO() {
		ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		launched = cdf.newChromeDriver(
				new CacheDirectoryName('Profile 17'),
				UserDataAccess.TO_GO)  // or 'Default'
		assertNotNull(launched)

		//println("ChromeDriver has been instantiated with profile directory Default")
		launched.getDriver().navigate().to('http://example.com/')
		launched.getDriver().quit()
	}

	@Test
	void test_newChromeDriver_disableViewersOfFlashAndPdf() {
		Path dir = outputFolder.resolve("test_newChromeDriver_disableViewersOfFlashAndPdf")
		Files.createDirectories(dir)
		String fileName = "SDG_DSD_MATRIX.1.7.xlsm"
		Path xlsm = dir.resolve(fileName)
		if (Files.exists(xlsm)) {
			Files.delete(xlsm)
		}
		ChromeDriverFactory cdf =
				ChromeDriverFactory.newChromeDriverFactory()
						.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
						.addChromePreferencesModifier(ChromePreferencesModifiers.downloadIntoDirectory(dir))
						.addChromePreferencesModifier(ChromePreferencesModifiers.disableViewersOfFlashAndPdf())
		launched = cdf.newChromeDriver()
		launched.getDriver().navigate().to("http://127.0.0.1/SDG_Guidlines_AUG_2019_Final.pdf");
		Thread.sleep(3000)
		launched.getDriver().quit()
	}

	@Test
	void test_newChromeDriver_downloadIntoDirectory() {
		Path dir = outputFolder.resolve("test_newChromeDriver_downloadIntoDirectory")
		Files.createDirectories(dir)
		String fileName = "SDG_DSD_MATRIX.1.7.xlsm"
		Path xlsm = dir.resolve(fileName)
		if (Files.exists(xlsm)) {
			Files.delete(xlsm)
		}
		ChromeDriverFactory cdf = ChromeDriverFactory
				.newChromeDriverFactory()
				.addChromePreferencesModifier(ChromePreferencesModifiers.downloadIntoDirectory(dir))
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		launched = cdf.newChromeDriver()
		System.out.println("test_newChromeDriver_downloadIntoDirectory: " + launched.getEmployedOptionsAsJSON().get())
		launched.getDriver().navigate().to("http://127.0.0.1/" + fileName)
		Thread.sleep(3000)
		assert Files.exists(xlsm)
		launched.getDriver().quit()
	}

	@Test
	void test_newChromeDriver_downloadWithoutPrompt() {
		Path dir = outputFolder.resolve("test_newChromeDriver_downloadWithoutPrompt")
		Files.createDirectories(dir)
		String fileName = "SDG_DSD_MATRIX.1.7.xlsm"
		Path xlsm = dir.resolve(fileName)
		if (Files.exists(xlsm)) {
			Files.delete(xlsm)
		}
		ChromeDriverFactory cdf = ChromeDriverFactory
				.newChromeDriverFactory()
				.addChromePreferencesModifier(ChromePreferencesModifiers.downloadIntoDirectory(dir))
				.addChromePreferencesModifier(ChromePreferencesModifiers.downloadWithoutPrompt())
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		launched = cdf.newChromeDriver()
		launched.getDriver().navigate().to("http://127.0.0.1/" + fileName)
		Thread.sleep(3000)
		assert Files.exists(xlsm)
		launched.getDriver().quit()
	}

	@Test
	void test_newChromeDriver_no_default_settings() {
		ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory(false)
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		launched = cdf.newChromeDriver()
		assertNotNull(launched)
		launched.getEmployedOptionsAsJSON().ifPresent({ json ->
			println("options is\n${json}")
		})
		//
		launched.getDriver().navigate().to('http://example.com/')
		//
		launched.getEmployedOptionsAsJSON().ifPresent({ String json ->
			/* in case "with default setting" you will see
		{
			"acceptSslCerts": true,
			"browserName": "chrome",
			"goog:chromeOptions": {
				"args": [
						"window-size=1024,768",
						...
		 */
			assertFalse("window-size option should not be there when no default setting",
					json.contains("window-size=1024,768")
			)
			launched.getDriver().quit()
		})
	}


	/**
	 * Basic case.
	 * Instantiate a ChromeDriver to open a Chrome browser with the default profile.
	 * 
	 */
	@Test
	void test_newChromeDriver_noUserProfileSpecified() {
		ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		launched = cdf.newChromeDriver()
		assertNotNull(launched)
		launched.getEmployedOptionsAsJSON().ifPresent({ json ->
			println("options is\n${json}")
		})
		//
		launched.getDriver().navigate().to('http://example.com/')
		launched.getDriver().quit()
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
		ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		launched = cdf.newChromeDriver(
				new UserProfile('Picasso'),
				UserDataAccess.FOR_HERE)
		assertNotNull(launched)
		launched.getDriver().navigate().to('http://example.com/')
		launched.getDriver().quit()
	}

	@Test
	void test_newChromeDriver_HEADLESS() {
		ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		launched = cdf.newChromeDriver(
				new UserProfile('Picasso'),
				UserDataAccess.TO_GO)
		assertNotNull(launched)
		launched.getDriver().navigate().to('http://example.com/')
		launched.getDriver().quit()
	}

	@Test
	void test_speed() {
		Timekeeper tk = new Timekeeper()
		Measurement m1 = new Measurement.Builder(
				"How long it took to open a Chrome browser",
				["Case"]).build()
		tk.add(new Table.Builder(m1).build())
		ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		// open Chrome as default
		LocalDateTime before = LocalDateTime.now()
		launched = cdf.newChromeDriver()
		LocalDateTime after = LocalDateTime.now()
		m1.recordDuration(["Case": "no specialization"], before, after)
		launched.getDriver().quit()

		// open Chrome with CacheDirectoryName
		before = LocalDateTime.now()
		launched = cdf.newChromeDriver(new CacheDirectoryName('Profile 17'))
		after = LocalDateTime.now()
		launched.getDriver().quit()
		m1.recordDuration(["Case": "with CacheDirectoryName"], before, after)
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
		ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
		cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
		cdf.pageLoadTimeout(10)
		launched = cdf.newChromeDriver()
		assertNotNull(launched)
		launched.getDriver().navigate().to("http://example.com")
		launched.getDriver().quit()
	}
}
