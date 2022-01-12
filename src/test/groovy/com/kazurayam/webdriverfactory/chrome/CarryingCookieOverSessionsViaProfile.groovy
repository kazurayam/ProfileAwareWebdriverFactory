package com.kazurayam.webdriverfactory.chrome

import com.kazurayam.webdriverfactory.CookieUtils
import com.kazurayam.webdriverfactory.UserProfile
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.Cookie
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.devtools.DevTools
import org.openqa.selenium.devtools.v96.network.model.ResponseReceived
import org.openqa.selenium.devtools.v96.network.model.ResponseReceivedExtraInfo
import org.openqa.selenium.devtools.v96.network.model.RequestWillBeSent
import org.openqa.selenium.devtools.v96.network.Network
import org.openqa.selenium.devtools.v96.network.model.Headers
import org.openqa.selenium.devtools.v96.network.model.RequestWillBeSentExtraInfo
import groovy.json.*

import static org.junit.Assert.*

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
        LaunchedChromeDriver launched

        // 1st session
        launched = factory.newChromeDriver(new UserProfile("Picasso"),
                ChromeDriverFactory.UserDataAccess.FOR_HERE)
        Cookie timestamp1 = observeCookie(launched)
        launched.getDriver().quit()   // at .quit(), the Cookies will be stored into disk

        // 2nd session
        launched = factory.newChromeDriver(new UserProfile("Picasso"),  // or new ProfileDirectoryName("Profile 6")
                ChromeDriverFactory.UserDataAccess.TO_GO)  // the Cookies file will be copied into the temp dir
        Cookie timestamp2 = observeCookie(launched)
        launched.getDriver().quit()
        //
        println "timestamp1 => " + CookieUtils.stringifyCookie(timestamp1)
        println "timestamp2 => " + CookieUtils.stringifyCookie(timestamp2)

        assertEquals(timestamp1.getValue(), timestamp2.getValue())
        assertNotEquals(timestamp1.getExpiry(), timestamp2.getExpiry())
    }

    private static Cookie observeCookie(LaunchedChromeDriver launched, String cookieName = "timestamp") {
        launched.getEmployedOptions()ifPresent({ options ->
            println "options => " + options.toString() })
        launched.getChromeUserProfile().ifPresent({ up ->
            println "userProfile => " + up.toString() })
        launched.getInstruction().ifPresent({ uda ->
            println "userDataAccess => " + uda.toString() })
        println "-------------------------------------------------"
        ChromeDriver driver = launched.getDriver()
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

    @BeforeClass
    static void beforeClass() {
        // setup the ChromeDriver binary
        WebDriverManager.chromedriver().setup()
    }
}
