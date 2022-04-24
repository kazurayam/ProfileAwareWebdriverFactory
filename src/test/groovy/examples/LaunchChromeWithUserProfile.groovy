package examples

import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

class LaunchChromeWithUserProfile {

    LaunchedChromeDriver launched

    @Test
    void test_launch_browser_with_profile() {
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        launched = factory.newChromeDriver(new UserProfile("Picasso"))
        launched.getDriver().navigate().to("http://example.com")
        Thread.sleep(1000)
    }

    @Test
    void test_launch_browser_with_profile_TO_GO() {
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        launched = factory.newChromeDriver(
                new UserProfile("Picasso"),
                ChromeDriverFactory.UserDataAccess.TO_GO)
        launched.getDriver().navigate().to("http://example.com")
        Thread.sleep(1000)
    }

    @Ignore
    @Test
    void test_launch_browser_with_profile_FOR_HERE() {
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        launched = factory.newChromeDriver(
                new UserProfile("Picasso"),
                ChromeDriverFactory.UserDataAccess.FOR_HERE)
        launched.getDriver().navigate().to("http://example.com")
        Thread.sleep(1000)
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
