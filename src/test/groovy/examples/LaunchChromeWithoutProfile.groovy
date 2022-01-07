package examples

import com.kazurayam.browserwindowlayout.BrowserWindowLayoutManager
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import io.github.bonigarcia.wdm.WebDriverManager

import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.Dimension
import org.openqa.selenium.Point
import org.openqa.selenium.chrome.ChromeDriver

class LaunchChromeWithoutProfile {

    ChromeDriver browser

    @Test
    void test_launch_browser() {
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        browser = factory.newChromeDriver()
        browser.navigate().to("http://example.com")
        Thread.sleep(1000)
    }

    @Test
    void test_launch_headless_browser() {
        ChromeDriverFactory factory = ChromeDriverFactory.newHeadlessChromeDriverFactory()
        browser = factory.newChromeDriver()
        browser.navigate().to("http://example.com")
        Thread.sleep(1000)
    }

    @Test
    void test_launch_and_layout_browser() {
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        browser = factory.newChromeDriver()
        Point position = new Point(100, 100)
        Dimension dimension = new Dimension(800, 600)
        BrowserWindowLayoutManager.layout(browser, position, dimension)
        browser.navigate().to("http://example.com")
        Thread.sleep(1000)
    }

    @BeforeClass
    static void beforeClass() {
        // setup the ChromeDriver binary
        WebDriverManager.chromedriver().setup()
    }

    @Before
    void setUp() {
        browser = null
    }

    @After
    void tearDown() {
        if (browser != null) {
            browser.quit()
            browser = null
        }
    }

}
