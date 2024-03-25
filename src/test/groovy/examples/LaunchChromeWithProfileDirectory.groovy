package examples

import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers
import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
import com.kazurayam.webdriverfactory.CacheDirectoryName
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

class LaunchChromeWithProfileDirectory {

    LaunchedChromeDriver launched

    @Test
    void test_launch_browser_with_profile_directory() {
        ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
        cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
        launched = cdf.newChromeDriver(new CacheDirectoryName("Profile 17"))
        launched.getDriver().navigate().to("http://example.com")
        Thread.sleep(1000)
    }

    @Test
    void test_launch_browser_with_profile_TO_GO() {
        ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
        cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
        launched = cdf.newChromeDriver(
                new CacheDirectoryName("Profile 17"),
                ChromeDriverFactory.UserDataAccess.TO_GO)
        launched.getDriver().navigate().to("http://example.com")
        Thread.sleep(1000)
    }

    @Ignore
    @Test
    void test_launch_browser_with_profile_FOR_HERE() {
        ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
        cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
        launched = cdf.newChromeDriver(
                new CacheDirectoryName("Profile 17"),
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
