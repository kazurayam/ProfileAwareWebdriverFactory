package com.kazurayam.webdriverfactory

import org.junit.Before
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

	private WebDriver driver

	@BeforeClass
	static void beforeClass() {
		WebDriverManager.chromedriver().setup()
	}

	@Before
	void setup() {
		driver = null
	}

	@After
	void quitWebDriver() {
		if (driver != null) {
			driver.quit()
		}
	}

	@Test
	void test_newWebDriver_Chrome() {
		WebDriverFactory wdf = new WebDriverFactory.Builder(DriverTypeName.CHROME_DRIVER).build()
		driver = wdf.newWebDriver()
		assertNotNull(driver)
	}

	@Test
	void test_getEmployedDesiredCapabilities() {
		WebDriverFactory wdf = new WebDriverFactory.Builder(DriverTypeName.CHROME_DRIVER).build()
		driver = wdf.newWebDriver()
		String json = wdf.getEmployedDesiredCapabilities()
		assertNotNull(json)
		println json
	}

	@Test
	void test_newWebDriver_Headless() {
		WebDriverFactory wdf = new WebDriverFactory.Builder(DriverTypeName.HEADLESS_DRIVER).build()
		driver = wdf.newWebDriver()
		assertNotNull(driver)
	}

	@Test
	void test_newWebDriver_Chrome_UserProfile() {
		WebDriverFactory wdf = new WebDriverFactory.Builder(DriverTypeName.CHROME_DRIVER)
				.userProfile('Katalon')
				.build()
		driver = wdf.newWebDriver()
		assertNotNull(driver)
	}

	@Test
	void test_newWebDriver_Headless_UserProfile() {
		WebDriverFactory wdf = new WebDriverFactory.Builder(DriverTypeName.HEADLESS_DRIVER)
				.userProfile('Katalon')
				.build()
		driver = wdf.newWebDriver()
		assertNotNull(driver)
	}
}
