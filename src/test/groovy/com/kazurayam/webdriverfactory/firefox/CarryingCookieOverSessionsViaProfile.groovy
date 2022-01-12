package com.kazurayam.webdriverfactory.firefox

import com.kazurayam.webdriverfactory.UserProfile
import groovy.json.JsonOutput
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.Cookie
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.devtools.DevTools
import org.openqa.selenium.devtools.v96.network.Network
import org.openqa.selenium.devtools.v96.network.model.ResponseReceived
import org.openqa.selenium.devtools.v96.network.model.ResponseReceivedExtraInfo
import org.openqa.selenium.devtools.v96.network.model.RequestWillBeSent
import org.openqa.selenium.devtools.v96.network.model.Headers
import org.openqa.selenium.devtools.v96.network.model.RequestWillBeSentExtraInfo

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotEquals

class CarryingCookieOverSessionsViaProfile {

    /**
     * This code requires the URL "http://127.0.0.1" is up and running.
     * To make it up, execute "> ./startup-server.sh".
     *
     * This code will open Firefox browser and navigate to the URL "http://127.0.0.1" twice.
     * The http server will send a cookie named "timestamp" with value of
     * 1. if the HTTP Request has no "timestamp" cookie, will create a new cookie with current timestamp
     * 2. if the HTTP Request sent a "timestamp" cookie, will echo it
     * The cookie has a expiry that lasts only 60 seconds.
     *
     * At the 1st time, the browser will be iopened with UserDataAccess option of "FOR_HERE".
     * Then the "timestamp" cookie will be persisted in the profile storage.
     *
     * At the 2nd time, the browser will be opened with UserDataAccess option of "TO_GO".
     * TO_GO means that the files in the profile directory will be copied from the genuine location
     * to the temporary location. Therefore I expect the cookies are carried over to
     * the second session. In the second session,
     * I expect the "timestamp" cookie should be sent from the browser to the server again.
     *
     * This code makes assertion if the values of "timestamp" cookie of the 1st session
     * and the 2nd session are equal.
     * If these are not equal, it means that cookie was not carried over.
     */
    @Test
    void test_carrying_cookie_over_sessions_via_profile() {
        FirefoxDriverFactory factory = FirefoxDriverFactory.newFirefoxDriverFactory()
        LaunchedFirefoxDriver launched

        // 1st session
        launched = factory.newFirefoxDriver(new UserProfile("Picasso"),
                FirefoxDriverFactory.UserDataAccess.FOR_HERE)
        Cookie timestamp1 = observeCookie(launched)
        launched.getDriver().quit()   // at .quit(), the Cookies will be stored into disk

        // 2nd session
        launched = factory.newFirefoxDriver(new UserProfile("Picasso"),  // or new ProfileDirectoryName("Profile 6")
                FirefoxDriverFactory.UserDataAccess.TO_GO)  // the Cookies file will be copied into the temp dir
        Cookie timestamp2 = observeCookie(launched)
        launched.getDriver().quit()
        //
        assertEquals(timestamp1.getValue(), timestamp2.getValue())
        assertNotEquals(timestamp1.getExpiry(), timestamp2.getExpiry())
    }

    private static Cookie observeCookie(LaunchedFirefoxDriver launched, String cookieName = "timestamp") {
        launched.getEmployedOptions()ifPresent({ options ->
            println "options => " + options.toString() })
        launched.getFirefoxUserProfile().ifPresent({ up ->
            println "userProfile => " + up.toString() })
        launched.getInstruction().ifPresent({ uda ->
            println "userDataAccess => " + uda.toString() })
        println "-------------------------------------------------"

        // FirefoxDriver in Selenium 4 does not support CDP,
        // so I have to comment out the following lines

        /*
        FirefoxDriver driver = launched.getDriver()
        DevTools devTool = driver.getDevTools()
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
         */
        //
        URL url = new URL("http://127.0.0.1")
        try {
            launched.getDriver().navigate().to(url.toString())
        } catch (Exception e) {
            throw new Exception("Possibly the URL ${url.toString()} is down. Start it up by executing \"./starup-server.sh\"", e)
        }
        Cookie timestamp = launched.getDriver().manage().getCookieNamed(cookieName)
        return timestamp
    }

    private static String stringifyHeaders(Headers headers) {
        String json = JsonOutput.toJson(headers)
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
