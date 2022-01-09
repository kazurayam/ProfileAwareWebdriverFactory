package examples

import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
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

    @Test
    void test_carrying_cookie_over_sessions_via_profile() {
        ChromeDriverFactory factory = ChromeDriverFactory.newHeadlessChromeDriverFactory()
        ChromeDriver browser

        // 1st session
        browser = factory.newChromeDriver(new UserProfile("Picasso"),
                ChromeDriverFactory.UserDataAccess.FOR_HERE)
        Cookie timestamp1 = processCookie(browser)
        browser.quit()

        // 2nd session
        browser = factory.newChromeDriver(new UserProfile("Picasso"),
                ChromeDriverFactory.UserDataAccess.TO_GO)
        Cookie timestamp2 = processCookie(browser)
        browser.quit()
        //
        assertEquals(timestamp1.getValue(), timestamp2.getValue())
        assertNotEquals(timestamp1.getExpiry(), timestamp2.getExpiry())
    }

    private Cookie processCookie(ChromeDriver browser, String cookieName = "timestamp") {
        DevTools devTool = browser.getDevTools()
        browser.userProfile.ifPresent({ up ->
            println "userProfile => " + up.toString() })
        browser.userDataAccess.ifPresent({ uda ->
            println "userDataAccess => " + uda.toString() })
        println "-------------------------------------------------"
        devTool.createSession()
        devTool.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()))
        // setting up the Network event listeners
        Optional<RequestWillBeSent> requestWillBeSent = Optional.empty()
        Optional<RequestWillBeSentExtraInfo> requestWillBeSentExtraInfo = Optional.empty()
        Optional<ResponseReceived> responseReceived = Optional.empty()
        Optional<ResponseReceivedExtraInfo> responseReceivedExtraInfo = Optional.empty()
        devTool.addListener(
                Network.requestWillBeSent(),
                { rs ->
                    requestWillBeSent = Optional.of(rs)
                })
        devTool.addListener(
                Network.requestWillBeSentExtraInfo(),
                {rsExtraInfo ->
                    requestWillBeSentExtraInfo = Optional.of(rsExtraInfo)
                }
        )
        devTool.addListener(
                Network.responseReceived(),
                {resReceived ->
                    responseReceived = Optional.of(resReceived)
                })
        devTool.addListener(
                Network.responseReceivedExtraInfo(),
                {resReceivedExtraInfo ->
                    responseReceivedExtraInfo = Optional.of(resReceivedExtraInfo)
                }
        )
        URL url = new URL("http://127.0.0.1")
        try {
            browser.navigate().to(url.toString())
            requestWillBeSent.ifPresent({ reqSent ->
                println "Request URL => ${reqSent.getRequest().getUrl()}"
                println "Request Method => ${reqSent.getRequest().getMethod()}"
                println "Request Headers => ${stringifyHeaders(reqSent.getRequest().getHeaders())}"
                println "-------------------------------------------------"
            })
            requestWillBeSentExtraInfo.ifPresent({ reqSentExtraInfo ->
                println "RequestExtraInfo Headers => ${stringifyHeaders(reqSentExtraInfo.getHeaders())}"
                println "-------------------------------------------------"
            })
            responseReceived.ifPresent({ resReceived ->
                println "Response URL => ${resReceived.getResponse().getUrl()}"
                println "Response Status => ${resReceived.getResponse().getStatus()}"
                println "Response Headers => ${stringifyHeaders(resReceived.getResponse().getHeaders())}"
                println "Response MIME Type => ${resReceived.getResponse().getMimeType().toString()}"
                println "-------------------------------------------------"
            })
            responseReceivedExtraInfo.ifPresent({ resReceivedExtraInfo ->
                println "ResponseExtraInfo Headers => ${stringifyHeaders(resReceivedExtraInfo.getHeaders())}"
                println "-------------------------------------------------"
            })
        } catch (Exception e) {
            throw new Exception("Possibly the URL ${url.toString()} is down. Start it up by executing \"./starup-server.sh\"", e)
        }
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
