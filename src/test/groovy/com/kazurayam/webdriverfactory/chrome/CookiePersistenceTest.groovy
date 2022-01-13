package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.UserProfile

import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.openqa.selenium.Cookie
import org.openqa.selenium.WebDriver

import static org.junit.Assert.assertNotNull
import java.util.concurrent.TimeUnit

/**
 * #2 Investigate how cookie is persisted and reloaded according to UserDataAccess.FOR_HERE and .TO_GO
 * https://github.com/kazurayam/webdriverfactory/issues/2
 *
 * Assumes that the site http://127.0.0.1:8090 is up and running.
 *
 * You can start it by `$ ./startup-server.sh`
 */
@Ignore
class CookiePersistenceTest {

    String url = "http://127.0.0.1:8090"
    String profileName = "Picasso"
    String cookieName = "timestamp"

    WebDriver driver


    @BeforeClass
    static void beforeClass() {
    }

    /**
     * make sure that the ChromeDriver activated with UserDataAccess.FOR_HERE option
     * can take over the cookie from a session to next session
     */
    @Test
    void test_FORHERE_takes_over_persisted_cookie() {
        ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
        // 1st session
        driver = cdFactory.newChromeDriver(new UserProfile(profileName),
                ChromeDriverFactory.UserDataAccess.FOR_HERE)
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
        driver.navigate().to(url)
        assertNotNull(driver)
        Cookie cookie1 = driver.manage().getCookieNamed(cookieName)
        String value1 = cookie1.getValue()
        String s = cookie1.toString()
        assertNotNull(s)

        // stop the 1st session
        driver.quit()

        // 2nd session
        driver = cdFactory.newChromeDriver(new UserProfile(profileName),
                ChromeDriverFactory.UserDataAccess.FOR_HERE)   // notice it! we use FOR_HERE
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
        driver.navigate().to(url)
        assertNotNull(driver)
        Cookie cookie2 = driver.manage().getCookieNamed(cookieName)
        String value2 = cookie2.getValue()

        // make sure that the cookie value was taken over via the profile
        println "cookie1 timestamp=${value1}"
        println "cookie2 timestamp=${value2}"
        if (value1 == value2) {
            println "cookie1 equals cookie2"
        } else {
            println "cookie1 DOES NOT equal cookie2"
        }
        assert value1 == value2
    }

    /**
     * make sure that the ChromeDriver activated with UserDataAccess.FOR_HERE option
     * can take over the cookie from a session to next session
     */
    @Test
    void test_TOGO_doesnt_take_over_persisted_cookie() {
        ChromeDriverFactory cdFactory = ChromeDriverFactory.newChromeDriverFactory()
        // 1st session
        driver = cdFactory.newChromeDriver(new UserProfile(profileName),
                ChromeDriverFactory.UserDataAccess.TO_GO)   // notice it; we use TO_GO!
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
        driver.navigate().to(url)
        assertNotNull(driver)
        Cookie cookie1 = driver.manage().getCookieNamed(cookieName)
        String value1 = cookie1.getValue()
        String s = cookie1.toString()
        assertNotNull(s)

        // stop the 1st session
        driver.quit()

        // 2nd session
        driver = cdFactory.newChromeDriver(new UserProfile(profileName),
                ChromeDriverFactory.UserDataAccess.FOR_HERE)
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
        driver.navigate().to(url)
        assertNotNull(driver)
        Cookie cookie2 = driver.manage().getCookieNamed(cookieName)
        String value2 = cookie2.getValue()

        // make sure that the cookie value was taken over via the profile
        println "cookie1 timestamp=${value1}"
        println "cookie2 timestamp=${value2}"
        if (value1 == value2) {
            println "cookie1 equals cookie2"
        } else {
            println "cookie1 DOES NOT equal cookie2"
        }
        assert value1 != value2
    }


    @BeforeClass
    static void setupClass() {
    }

    @Before
    void setup() {}

    @After
    void teardown() {
        if (driver != null) {
            driver.quit()
            driver = null
        }
    }

}
