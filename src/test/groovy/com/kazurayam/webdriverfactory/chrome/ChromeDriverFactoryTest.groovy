package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.UserProfile
import org.junit.BeforeClass

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.openqa.selenium.Cookie
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities

import io.github.bonigarcia.wdm.WebDriverManager

/**
 * @author kazurayam
 */
@RunWith(JUnit4.class)
class ChromeDriverFactoryTest {

	@BeforeClass
	static void beforeClass() {
		WebDriverManager.chromedriver().setup()
	}

	@Before
	void setup() {}

	/**
	 * Basic case.
	 * Instantiate a ChromeDriver to open a Chrome browser with the default profile.
	 * 
	 */
	@Test
	void test_newChromeDriver_withoutUserProfile() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newInstance()
		WebDriver driver = cdFactory.newChromeDriver()
		assertNotNull(driver)

		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		//println("DesiredCapabilities: ${dc.toString()}")
		//assertThat(cdFactory.getChromeProfile().getName(), is('kazurayam'))

		driver.navigate().to('http://demoaut.katalon.com/')
		driver.quit()
	}

	/**
	 * Instantiate a ChromeDriver to open a Chrome browser specifying a user profile "Katalon"
	 * while cloning the User Data directory to a temporary folder
	 */
	@Test
	void test_newChromeDriver_withUserProfile_CLONE() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newInstance()
		WebDriver driver = cdFactory.newChromeDriver(new UserProfile('Katalon'))
		assertNotNull(driver)

		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		println("DesiredCapabilities: ${dc.toString()}")

		println("ChromeDriver has been instantiated with profile Katalon")
		driver.navigate().to('http://demoaut.katalon.com/')
		driver.quit()
	}

	/**
	 * Instantiate a ChromeDriver to open a Chrome browser specifying a user profile "Katalon"
	 * while cloning the User Data directory to a temporary folder
	 * You are likely to see an error:
	 * > invalid argument: user data directory is already in use, please specify a unique value for --user-data-dir argument, or don't use --user-data-dir
	 * when you have at least 1 Chrome browser already opened.
	 */
	@Test
	void test_newChromeDriver_withUserProfile_LOCK() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newInstance()
		WebDriver driver = cdFactory.newChromeDriver(
				new UserProfile('Katalon'),
				ChromeDriverFactory.UserDataAccess.LOCK_USER_DATA)
		assertNotNull(driver)

		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		println("DesiredCapabilities: ${dc.toString()}")

		driver.navigate().to('http://demoaut.katalon.com/')
		driver.quit()
	}

	/**
	 * open a session using a user profile Katalon and navigate too http://localhost, then close the session.
	 * the session will create a cookie "timestamp".
	 * open a second session using Katalon Profile again.
	 * I expect the second session will use the same value of timestamp cookie. So I test it.
	 * 
	 */
	@Ignore
	@Test
	public void test_if_cookie_is_retained_in_profile_accross_2_sessions() {
		// we want Headless
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newInstance()
		//ChromeOptionsModifier com = new ChromeOptionsModifierHeadless()
		//cdFactory.addChromeOptionsModifier(com)
		//
		String url = 'http://localhost/'
		// 1st session
		WebDriver driver = cdFactory.newChromeDriver(new UserProfile('Katalon'))
		driver.navigate().to(url)
		Set<Cookie> cookies = driver.manage().getCookies()
		println "1st session: " + cookies
		String phpsessid1st = driver.manage().getCookieNamed('timestamp')
		driver.quit()

		// 2nd session
		driver = cdFactory.newChromeDriver(new UserProfile('Katalon'))
		driver.navigate().to(url)
		cookies = driver.manage().getCookies()
		println "2nd session: " + cookies
		String phpsessid2nd = driver.manage().getCookieNamed('timestamp')
		driver.quit()
		//
		assert phpsessid1st == phpsessid2nd;
	}
}
