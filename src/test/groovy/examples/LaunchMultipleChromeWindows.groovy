package examples

import com.kazurayam.browserwindowlayout.BrowserWindowLayoutManager
import com.kazurayam.browserwindowlayout.TilingWindowLayoutMetrics
import com.kazurayam.browserwindowlayout.WindowLocation
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
    TilingWindowLayoutMetrics tilingLayout

    @Test
    void test_open2windows_in_tiling_layout() {
        List<WindowLocation> locations = [
                new WindowLocation(2, 0),
                new WindowLocation(2, 1),
        ]
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        ChromeDriver browser0 = factory.newChromeDriver(new UserProfile("Picasso"))
        BrowserWindowLayoutManager.layout(browser0,
                tilingLayout.getWindowPosition(locations.get(0)),
                tilingLayout.getWindowDimension(locations.get(0)))
        ChromeDriver browser1 = factory.newChromeDriver(new UserProfile("Gogh"))
        BrowserWindowLayoutManager.layout(browser1,
                tilingLayout.getWindowPosition(locations.get(1)),
                tilingLayout.getWindowDimension(locations.get(1)))
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
        tilingLayout = new TilingWindowLayoutMetrics.Builder().build()
    }

    @After
    void tearDown() {
        if (browser != null) {
            browser.quit()
            browser = null
        }
    }
}
