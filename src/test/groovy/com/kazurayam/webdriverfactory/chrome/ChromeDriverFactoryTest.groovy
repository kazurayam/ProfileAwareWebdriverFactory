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
	void test_newChromeDriver() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newInstance()
		WebDriver driver = cdFactory.newChromeDriver()
		assertNotNull(driver)

		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		println("DesiredCapabilities: ${dc.toString()}")
		//assertThat(cdFactory.getChromeProfile().getName(), is('kazurayam'))

		driver.navigate().to('http://demoaut.katalon.com/')
		driver.quit()
	}

	/**
	 * Instantiate a ChromeDriver to open a Chrome browser specifying a user profile "Katalon"
	 * 
	 */
	@Test
	void test_newChromeDriverWithUserProfileName() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newInstance()
		WebDriver driver = cdFactory.newChromeDriverWithUserProfile(new UserProfile('Katalon'))
		assertNotNull(driver)

		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		println("DesiredCapabilities: ${dc.toString()}")

		println("ChromeDriver has been instantiated with profile Katalon")
		driver.navigate().to('http://demoaut.katalon.com/')
		driver.quit()
	}

	/**
	 * Instantiate a ChromeDriver to open Chrome browser specifying a profile directory "Default"
	 * 
	 */
	@Test
	void test_newChromeDriverWithProfileDirectoryName() {
		ChromeDriverFactory cdFactory = ChromeDriverFactory.newInstance()
		WebDriver driver = cdFactory.newChromeDriverWithProfileDirectoryName('Default')
		assertNotNull(driver)

		DesiredCapabilities dc = cdFactory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		println("DesiredCapabilities: ${dc.toString()}")

		println("ChromeDriver has been instantiated with profile directory Default")
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
		WebDriver driver = cdFactory.newChromeDriverWithUserProfile(new UserProfile('Katalon'))
		driver.navigate().to(url)
		Set<Cookie> cookies = driver.manage().getCookies()
		println "1st session: " + cookies
		String phpsessid1st = driver.manage().getCookieNamed('timestamp')
		driver.quit()

		// 2nd session
		driver = cdFactory.newChromeDriverWithUserProfile(new UserProfile('Katalon'))
		driver.navigate().to(url)
		cookies = driver.manage().getCookies()
		println "2nd session: " + cookies
		String phpsessid2nd = driver.manage().getCookieNamed('timestamp')
		driver.quit()
		//
		assert phpsessid1st == phpsessid2nd;
	}
}
