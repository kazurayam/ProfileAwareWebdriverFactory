package examples

import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.ProfileDirectoryName
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.Cookie
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.devtools.DevTools
import org.openqa.selenium.devtools.v85.network.model.ResponseReceived
import org.openqa.selenium.devtools.v85.network.model.ResponseReceivedExtraInfo
import org.openqa.selenium.devtools.v96.network.model.RequestWillBeSent
import org.openqa.selenium.devtools.v96.network.Network
import org.openqa.selenium.devtools.v96.network.model.Headers
import org.openqa.selenium.devtools.v96.network.model.RequestWillBeSentExtraInfo
import groovy.json.*

import static org.junit.Assert.*

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CarryingCookieOverSessionsViaProfile {

    /**
     * This code requires the URL "http://127.0.0.1" is up and running.
     * To make it up, execute "> ./startup-server.sh".
     *
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
     * the second session. In the second session,
     * I expect the "timestamp" cookie should be sent from Chrome to the server again.
     *
     * This code makes assertion if the values of "timestamp" cookie of the 1st session
     * and the 2nd session are equal.
     * If these are not equal, it means that cookie was not carried over.
     */
    @Test
    void test_carrying_cookie_over_sessions_via_profile() {
        //ChromeDriverFactory factory = ChromeDriverFactory.newHeadlessChromeDriverFactory()
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        ChromeDriver browser

        // 1st session
        browser = factory.newChromeDriver(new UserProfile("Picasso"),
                ChromeDriverFactory.UserDataAccess.FOR_HERE)
        Cookie timestamp1 = observeCookie(browser)
        browser.quit()   // at .quit(), the Cookies will be stored into disk

        // 2nd session
        browser = factory.newChromeDriver(new UserProfile("Picasso"),  // or new ProfileDirectoryName("Profile 6")
                ChromeDriverFactory.UserDataAccess.TO_GO)  // the Cookies file will be copied into the temp dir
        Cookie timestamp2 = observeCookie(browser)
        browser.quit()
        //
        assertEquals(timestamp1.getValue(), timestamp2.getValue())
        assertNotEquals(timestamp1.getExpiry(), timestamp2.getExpiry())
    }

    private Cookie observeCookie(ChromeDriver browser, String cookieName = "timestamp") {
        browser.desiredCapabilities.ifPresent({ dc ->
            println "DesiredCapabilities => " + dc.toString() })
        browser.userProfile.ifPresent({ up ->
            println "userProfile => " + up.toString() })
        browser.userDataAccess.ifPresent({ uda ->
            println "userDataAccess => " + uda.toString() })
        println "-------------------------------------------------"
        DevTools devTool = browser.getDevTools()
        devTool.createSession()
        devTool.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()))
        // setting up the Network event listeners
        Optional<RequestWillBeSent> requestWillBeSent = Optional.empty()
        Optional<RequestWillBeSentExtraInfo> requestWillBeSentExtraInfo = Optional.empty()
        Optional<ResponseReceived> responseReceived = Optional.empty()
        Optional<ResponseReceivedExtraInfo> responseReceivedExtraInfo = Optional.empty()
        devTool.addListener(
                Network.requestWillBeSent(),
                { reqSent ->
                    println "Request URL => ${reqSent.getRequest().getUrl()}"
                    println "Request Method => ${reqSent.getRequest().getMethod()}"
                    println "Request Headers => ${stringifyHeaders(reqSent.getRequest().getHeaders())}"
                    println "-------------------------------------------------"
                })
        devTool.addListener(
                Network.requestWillBeSentExtraInfo(),
                { reqSentExtraInfo ->
                    println "RequestExtraInfo Headers => ${stringifyHeaders(reqSentExtraInfo.getHeaders())}"
                    println "-------------------------------------------------"
                }
        )
        devTool.addListener(
                Network.responseReceived(),
                { resReceived ->
                    println "Response URL => ${resReceived.getResponse().getUrl()}"
                    println "Response Status => ${resReceived.getResponse().getStatus()}"
                    println "Response Headers => ${stringifyHeaders(resReceived.getResponse().getHeaders())}"
                    println "Response MIME Type => ${resReceived.getResponse().getMimeType().toString()}"
                    println "-------------------------------------------------"
                })
        devTool.addListener(
                Network.responseReceivedExtraInfo(),
                { resReceivedExtraInfo ->
                    println "ResponseExtraInfo Headers => ${stringifyHeaders(resReceivedExtraInfo.getHeaders())}"
                    println "-------------------------------------------------"
                }
        )
        //
        URL url = new URL("http://127.0.0.1")
        try {
            browser.navigate().to(url.toString())
        } catch (Exception e) {
            throw new Exception("Possibly the URL ${url.toString()} is down. Start it up by executing \"./starup-server.sh\"", e)
        }
        Cookie timestamp = browser.manage().getCookieNamed(cookieName)
        return timestamp
    }

    private static String stringifyHeaders(Headers headers) {
        String json = JsonOutput.toJson(headers.delegate())
        String pp = JsonOutput.prettyPrint(json)
        return pp
    }


    /**
     * @returns "timestamp=Sat, 08 Jan 2022 05:13:04 GMT; expires=Sat, 08 Jan 2022 05:13:34 GMT; path=/; domain=127.0.0.1"
     */
    private static String stringifyCookie(Cookie cookie) {
        StringBuilder sb = new StringBuilder()
        sb.append(cookie.getName())
        sb.append("=")
        sb.append(cookie.getValue())
        sb.append("; ")
        sb.append("expires=")
        sb.append(formatDateInRFC7231(cookie.getExpiry()))
        sb.append("; path=")
        sb.append(cookie.getPath())
        sb.append("; domain=")
        sb.append(cookie.getDomain())
        return sb.toString()
    }

    private static String formatDateInRFC7231(Date date) {
        ZoneId zid = ZoneId.systemDefault()
        ZonedDateTime zdt = ZonedDateTime.ofInstant(date.toInstant(), zid)
        String formatted = rfc7231.format(zdt)
        return formatted
    }

    private static final DateTimeFormatter rfc7231 = DateTimeFormatter
            .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
            .withZone(ZoneId.of("GMT"))


    @BeforeClass
    static void beforeClass() {
        // setup the ChromeDriver binary
        WebDriverManager.chromedriver().setup()
    }
}
