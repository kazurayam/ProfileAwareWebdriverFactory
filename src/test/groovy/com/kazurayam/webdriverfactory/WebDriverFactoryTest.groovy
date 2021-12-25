package com.kazurayam.webdriverfactory

import org.junit.BeforeClass

import static org.junit.Assert.*

import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.openqa.selenium.WebDriver

import io.github.bonigarcia.wdm.WebDriverManager

@RunWith(JUnit4.class)
class WebDriverFactoryTest {

	WebDriver driver

	@BeforeClass
	static void beforeClass() {
		WebDriverManager.chromedriver().setup()

		//String ght = TestUtils.getGitHubPersonalAccessToken()
		//WebDriverManager.firefoxdriver().gitHubToken(ght).setup()
	}

	@After
	void quitWebDriver() {
		if (driver != null) {
			driver.quit()
			driver = null
		}
	}

	@Test
	void test_newWebDriver_Chrome() {
		driver = WebDriverFactory.newWebDriver(DriverTypeName.CHROME_DRIVER)
		assertNotNull(driver)
	}

	@Test
	void test_newWebDriver_Headless() {
		driver = WebDriverFactory.newWebDriver(DriverTypeName.HEADLESS_DRIVER)
		assertNotNull(driver)
	}

	@Test
	void test_newWebDriver_Chrome_UserProfile() {
		driver = WebDriverFactory.newWebDriver(
				DriverTypeName.CHROME_DRIVER, new UserProfile('Katalon'))
		assertNotNull(driver)
	}

	@Test
	void test_newWebDriver_Headless_UserProfile() {
		driver = WebDriverFactory.newWebDriver(
				DriverTypeName.HEADLESS_DRIVER, new UserProfile('Katalon'))
		assertNotNull(driver)
	}
}
