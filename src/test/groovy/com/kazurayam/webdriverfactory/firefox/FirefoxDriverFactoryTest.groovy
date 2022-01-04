package com.kazurayam.webdriverfactory.firefox

import org.junit.BeforeClass

import static org.hamcrest.CoreMatchers.*
import static org.junit.Assert.*

import org.junit.Ignore
import org.junit.Test
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities

import io.github.bonigarcia.wdm.WebDriverManager

@Ignore
class FirefoxDriverFactoryTest {

	@BeforeClass
	static void beforeClass() {
		WebDriverManager.firefoxdriver().setup()
	}

	@Ignore
	@Test
	void test_newFirefoxDriver_noArg() {
		FirefoxDriverFactory factory = FirefoxDriverFactory.newInstance()
		WebDriver driver = factory.newFirefoxDriver()
		assert driver != null
		driver.quit()
	}

	/**
	 * Instantiate a FirefoxDriver to open a Firefox browser specifying a user profile "Picasso"
	 *
	 */
	@Test
	void test_newFirefoxDriverWithProfile() {
		FirefoxDriverFactory factory = FirefoxDriverFactory.newInstance()
		WebDriver driver = factory.newFirefoxDriverWithProfile('Picasso')
		assertThat(driver, is(notNullValue()))

		DesiredCapabilities dc = factory.getEmployedDesiredCapabilities()
		assertNotNull(dc)
		println("DesiredCapabilities: ${dc.toString()}")

		println("ChromeDriver has been instantiated with profile Picasso")
		driver.navigate().to('http://example.com/')
		driver.quit()
	}
}
