package examples

import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers
import io.github.bonigarcia.wdm.WebDriverManager

import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class LaunchChromeWithoutProfile {

    LaunchedChromeDriver launched

    @Test
    void test_launch_browser() {
        ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
        cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
        launched = cdf.newChromeDriver()
        launched.getDriver().navigate().to("http://example.com")
        Thread.sleep(1000)
    }

    @Test
    void test_launch_headless_browser() {
        ChromeDriverFactory cdf = ChromeDriverFactory.newHeadlessChromeDriverFactory()
        cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
        launched = cdf.newChromeDriver()
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
