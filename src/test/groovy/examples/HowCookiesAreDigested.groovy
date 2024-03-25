package examples

import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers
import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
import com.kazurayam.webdriverfactory.CacheDirectoryName
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class HowCookiesAreDigested {

    LaunchedChromeDriver launched

    @Test
    void test_launch_browser_with_profile_TO_GO() {
        ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
        cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
        launched = cdf.newChromeDriver(
                new UserProfile("Kazuaki"),
                ChromeDriverFactory.UserDataAccess.TO_GO)
        launched.getDriver().navigate().to("https://mail.google.com/mail/u/0/#inbox")
        Thread.sleep(1000)
        launched.getDriver().quit()
    }

    @Test
    void test_launch_browser_withCacheDirectoryName_TO_GO() {
        ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
        cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
        launched = cdf.newChromeDriver(
                new CacheDirectoryName("Default"),
                ChromeDriverFactory.UserDataAccess.TO_GO)
        launched.getDriver().navigate().to("https://mail.google.com/mail/u/0/#inbox")
        Thread.sleep(1000)
        launched.getDriver().quit()
    }

    @BeforeClass
    static void beforeClass() {
        // setup the ChromeDriver binary
        WebDriverManager.chromedriver().setup()
    }

    @Before
    void setUp() {
        launched = null
    }

    @After
    void tearDown() {
        if (launched != null) {
            launched.getDriver().quit()
            launched = null
        }
    }

}
