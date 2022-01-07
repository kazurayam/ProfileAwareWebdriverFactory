package examples

import com.kazurayam.browserwindowlayout.BrowserWindowLayoutManager
import com.kazurayam.browserwindowlayout.TilingWindowLayoutMetrics

import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.chrome.ChromeDriver

class LaunchMultipleChromeWindows {

    ChromeDriver browser

    @Test
    void test_open2windows_in_tiling_layout() {
        TilingWindowLayoutMetrics tilingLayout = new TilingWindowLayoutMetrics.Builder(2).build()
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        ChromeDriver browser0 = factory.newChromeDriver(new UserProfile("Picasso"))
        BrowserWindowLayoutManager.layout(browser0,
                tilingLayout.getWindowPosition(0),
                tilingLayout.getWindowDimension(0))
        ChromeDriver browser1 = factory.newChromeDriver(new UserProfile("Gogh"))
        BrowserWindowLayoutManager.layout(browser1,
                tilingLayout.getWindowPosition(1),
                tilingLayout.getWindowDimension(1))
        browser0.navigate().to("https://www.pablopicasso.org/")
        browser1.navigate().to("https://www.vincentvangogh.org/")
        Thread.sleep(1000)
        browser0.quit()
        browser1.quit()
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
