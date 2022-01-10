package examples

import com.kazurayam.browserwindowlayout.BrowserWindowLayoutManager
import com.kazurayam.browserwindowlayout.TilingWindowLayoutMetrics

import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.chrome.ChromeDriver

class LaunchMultipleChromeWindowsInTilingLayout {

    @Test
    void test_open2windows_in_tiling_layout() {
        TilingWindowLayoutMetrics layoutMetrics = new TilingWindowLayoutMetrics.Builder(2).build()
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        LaunchedChromeDriver launched0 = factory.newChromeDriver(new UserProfile("Picasso"))
        BrowserWindowLayoutManager.layout(launched0.getDriver(),
                layoutMetrics.getWindowPosition(0),
                layoutMetrics.getWindowDimension(0))
        LaunchedChromeDriver launched1 = factory.newChromeDriver(new UserProfile("Gogh"))
        BrowserWindowLayoutManager.layout(launched1.getDriver(),
                layoutMetrics.getWindowPosition(1),
                layoutMetrics.getWindowDimension(1))
        launched0.getDriver().navigate().to("https://en.wikipedia.org/wiki/Pablo_Picasso")
        launched1.getDriver().navigate().to("https://en.wikipedia.org/wiki/Vincent_van_Gogh")
        Thread.sleep(1000)
        launched0.getDriver().quit()
        launched1.getDriver().quit()
    }

    @BeforeClass
    static void beforeClass() {
        // setup the ChromeDriver binary
        WebDriverManager.chromedriver().setup()
    }
}
