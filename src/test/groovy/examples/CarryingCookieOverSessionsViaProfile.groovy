package examples

import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.Cookie
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.devtools.DevTools
import org.openqa.selenium.devtools.v96.network.Network
import org.openqa.selenium.devtools.v96.network.model.Headers
import groovy.json.*

import static org.junit.Assert.*

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CarryingCookieOverSessionsViaProfile {

    @Test
    void test_carrying_cookie_over_sessions_via_profile() {
        ChromeDriverFactory factory = ChromeDriverFactory.newHeadlessChromeDriverFactory()
        ChromeDriver browser

        // 1st session
        browser = factory.newChromeDriver(new UserProfile("Picasso"), ChromeDriverFactory.UserDataAccess.FOR_HERE)
        Cookie timestamp1 = processCookie(browser)
        browser.quit()

        // 2nd session
        browser = factory.newChromeDriver(new UserProfile("Picasso"), ChromeDriverFactory.UserDataAccess.TO_GO)
        Cookie timestamp2 = processCookie(browser)
        browser.quit()
        //
        assertEquals(timestamp1.getValue(), timestamp2.getValue())
        assertNotEquals(timestamp1.getExpiry(), timestamp2.getExpiry())
    }

    private Cookie processCookie(ChromeDriver browser, String cookieName = "timestamp") {
        DevTools devTool = browser.getDevTools()
        devTool.createSession()
        devTool.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()))
        devTool.addListener(
                Network.requestWillBeSent(),
                { requestSent ->
                    println "Request URL => ${requestSent.getRequest().getUrl()}"
                    println "Request Method => ${requestSent.getRequest().getMethod()}"
                    println "Request Headers => ${stringifyHeaders(requestSent.getRequest().getHeaders())}"
                    println "-------------------------------------------------"
                })
        devTool.addListener(
                Network.requestWillBeSentExtraInfo(),
                {requestWillBeSentExtraInfo ->
                    println "RequestExtraInfo Headers => ${stringifyHeaders(requestWillBeSentExtraInfo.getHeaders())}"
                    println "-------------------------------------------------"
                }
        )
        devTool.addListener(
                Network.responseReceived(),
                {responseReceived ->
                    println "Response URL => ${responseReceived.getResponse().getUrl()}"
                    println "Response Status => ${responseReceived.getResponse().getStatus()}"
                    println "Response Headers => ${stringifyHeaders(responseReceived.getResponse().getHeaders())}"
                    println "Response MIME Type => ${responseReceived.getResponse().getMimeType().toString()}"
                    println "-------------------------------------------------"
                })
        devTool.addListener(
                Network.responseReceivedExtraInfo(),
                {responseReceivedExtraInfo ->
                    println "ResponseExtraInfo Headers => ${stringifyHeaders(responseReceivedExtraInfo.getHeaders())}"
                    println "-------------------------------------------------"
                }
        )
        browser.navigate().to("http://127.0.0.1")
        Cookie timestamp = browser.manage().getCookieNamed(cookieName)
        return timestamp
    }

    private String stringifyHeaders(Headers headers) {
        String json = JsonOutput.toJson(headers.delegate())
        String pp = JsonOutput.prettyPrint(json)
        return pp
    }


    /**
     * @returns "timestamp=Sat, 08 Jan 2022 05:13:04 GMT; expires=Sat, 08 Jan 2022 05:13:34 GMT; path=/; domain=127.0.0.1"
     */
    private String stringifyCookie(Cookie cookie) {
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

    private String formatDateInRFC7231(Date date) {
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
