package examples

import com.kazurayam.browserwindowlayout.BrowserWindowLayoutManager
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
import io.github.bonigarcia.wdm.WebDriverManager

import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.Dimension
import org.openqa.selenium.Point

class LaunchChromeWithoutProfile {

    LaunchedChromeDriver launched

    @Test
    void test_launch_browser() {
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        launched = factory.newChromeDriver()
        launched.getDriver().navigate().to("http://example.com")
        Thread.sleep(1000)
    }

    @Test
    void test_launch_headless_browser() {
        ChromeDriverFactory factory = ChromeDriverFactory.newHeadlessChromeDriverFactory()
        launched = factory.newChromeDriver()
        launched.getDriver().navigate().to("http://example.com")
        Thread.sleep(1000)
    }

    @Test
    void test_launch_and_layout_browser() {
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        launched = factory.newChromeDriver()
        Point position = new Point(100, 100)
        Dimension dimension = new Dimension(800, 600)
        BrowserWindowLayoutManager.layout(launched.getDriver(), position, dimension)
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
