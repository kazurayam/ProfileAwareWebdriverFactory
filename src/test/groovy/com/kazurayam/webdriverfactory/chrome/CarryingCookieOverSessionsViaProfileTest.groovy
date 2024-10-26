package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.CookieServer
import com.kazurayam.webdriverfactory.CookieUtils
import com.kazurayam.webdriverfactory.UserProfile
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.openqa.selenium.Cookie

import java.nio.file.Paths

import static org.junit.Assert.*

/**
 * I marked this test as @Ignore.
 * Why?
 * Because I know that this test fails when I run it while a Chrome browser is already opened.
 * The test fails because the FOR_HERE option.
 *
 * However this test is valuable. It demonstrates how the ChromeDriverFactory class
 * can help you carry over cookie info over HTTP sessions via Chrome profile.
 *
 */
@Ignore
class CarryingCookieOverSessionsViaChromeProfileTest {

    private CookieServer cookieServer

    @BeforeClass
    static void beforeClass() {
        // setup the ChromeDriver binary
        WebDriverManager.chromedriver().clearDriverCache().setup()
    }

    @Before
    void setup() {
        cookieServer = new CookieServer()
        cookieServer.setBaseDir(Paths.get("./src/web"))
        cookieServer.setPrintRequestRequired(true);
        cookieServer.setDebugMode(true)
        cookieServer.startup()
    }

    /**
     * This code will open Chrome browser and navigate to the URL "http://127.0.0.1" twice.
     * The http server will send a cookie named "timestamp" with value of
     * 1. if the HTTP Request has no "timestamp" cookie, will create a new cookie with current timestamp
     * 2. if the HTTP Request sent a "timestamp" cookie, will echo it
     * The cookie has a expiry that lasts only 60 seconds.
     *
     * At the 1st time, Chrome is opened with UserDataAccess option of "FOR_HERE".
     * Then the "timestamp" cookie will be persisted in the profile storage.
     *
     * At the 2nd time, Chrome is opened with UserDataAccess option of "TO_GO".
     * TO_GO means that the files in the profile directory will be copied from the genuine location
     * to the temporary location. Therefore I expect the cookies are carried over to
     * the second session. In the second session, I expect the "timestamp" cookie
     * should be sent from Chrome to the server again.
     *
     * This code makes assertion if the values of "timestamp" cookie of the 1st session
     * and the 2nd session are equal.
     * If these are not equal, it means that cookie was not carried over.
     *
     */

    @Test
    void test_automationNotEnabled_nonHeadless() {
        ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
        // automation not enabled
        //cdf.addChromeOptionsModifier(ChromeOptionsModifiers.enableAutomation())
        Tuple2<Cookie, Cookie> result = performConsecutiveSessions(cdf)
        assertEquals(result.first.getValue(), result.second.getValue())
    }

    @Test
    void test_automationEnabled_nonHeadless() {
        ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
        // automation is enabled
        cdf.addChromeOptionsModifier(ChromeOptionsModifiers.enableAutomation())
        //
        Tuple2<Cookie, Cookie> result = performConsecutiveSessions(cdf)
        assertEquals(result.first.getValue(), result.second.getValue())
    }

    //-------------------------------------------------------------------------

    @Test
    void test_automationIsNotEnabled_Headless() {
        ChromeDriverFactory cdf =
                ChromeDriverFactory.newHeadlessChromeDriverFactory()
        // automation is not enabled
        //cdf.addChromeOptionsModifier(ChromeOptionsModifiers.enableAutomation())
        //
        Tuple2<Cookie, Cookie> result = performConsecutiveSessions(cdf)
        assertEquals(result.first.getValue(), result.second.getValue())
    }

    @Test
    void test_automationIsEnabled_Headless() {
        ChromeDriverFactory cdf =
                ChromeDriverFactory.newHeadlessChromeDriverFactory()
        // automation is enabled
        cdf.addChromeOptionsModifier(ChromeOptionsModifiers.enableAutomation())
        //
        Tuple2<Cookie, Cookie> result = performConsecutiveSessions(cdf)
        assertEquals(result.first.getValue(), result.second.getValue())
    }

    //=========================================================================

    private static Tuple2<Cookie, Cookie> performConsecutiveSessions(ChromeDriverFactory cdf) {
        LaunchedChromeDriver launched
        // 1st session
        launched = cdf.newChromeDriver(new UserProfile("Picasso"),
                ChromeDriverFactory.UserDataAccess.FOR_HERE)
        Cookie timestamp1 = observeCookie(launched)
        launched.getDriver().quit()
        // at .quit(), the Cookies will be stored into the genuine UserDataDir

        try {
            Thread.sleep(3000)
        } catch (Exception e) {}

        // 2nd session
        launched = cdf.newChromeDriver(
                new UserProfile("Picasso"),
                ChromeDriverFactory.UserDataAccess.TO_GO
                // the Cookies file will be copied from the genuine UserDataDir
                // into the temp dir
        )
        Cookie timestamp2 = observeCookie(launched)
        launched.getDriver().quit()
        //
        println "timestamp1 => " + CookieUtils.stringifyCookie(timestamp1)
        println "timestamp2 => " + CookieUtils.stringifyCookie(timestamp2)
        return new Tuple2(timestamp1, timestamp2)
    }

    private static Cookie observeCookie(LaunchedChromeDriver launched, String cookieName = "timestamp") {
        launched.getEmployedOptions().ifPresent({ options ->
            println "options => " + options.toString() })
        launched.getChromeUserProfile().ifPresent({ up ->
            println "userProfile => " + up.toString() })
        launched.getInstruction().ifPresent({ uda ->
            println "userDataAccess => " + uda.toString() })

        URL url = new URL("http://127.0.0.1")
        try {
            launched.getDriver().navigate().to(url.toString())
        } catch (Exception e) {
            throw new Exception("Possibly the URL ${url.toString()} is down. Start it up by executing \"./starup-server.sh\"", e)
        }
        return launched.getDriver().manage().getCookieNamed(cookieName)
    }

    @After
    void tearDown() {
        if (cookieServer != null) {
            cookieServer.shutdown()
        }
    }
}