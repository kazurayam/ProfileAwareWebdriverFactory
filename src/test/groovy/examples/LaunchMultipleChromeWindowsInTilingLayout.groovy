package examples

import com.kazurayam.browserwindowlayout.BrowserWindowLayoutManager
import com.kazurayam.browserwindowlayout.TilingWindowLayoutMetrics

import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.chrome.ChromeDriver

class LaunchMultipleChromeWindowsInTilingLayout {

    @Test
    void test_open2windows_in_tiling_layout() {
        TilingWindowLayoutMetrics layoutMetrics = new TilingWindowLayoutMetrics.Builder(2).build()
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        ChromeDriver browser0 = factory.newChromeDriver(new UserProfile("Picasso"))
        BrowserWindowLayoutManager.layout(browser0,
                layoutMetrics.getWindowPosition(0),
                layoutMetrics.getWindowDimension(0))
        ChromeDriver browser1 = factory.newChromeDriver(new UserProfile("Gogh"))
        BrowserWindowLayoutManager.layout(browser1,
                layoutMetrics.getWindowPosition(1),
                layoutMetrics.getWindowDimension(1))
        browser0.navigate().to("https://en.wikipedia.org/wiki/Pablo_Picasso")
        browser1.navigate().to("https://en.wikipedia.org/wiki/Vincent_van_Gogh")
        Thread.sleep(1000)
        browser0.quit()
        browser1.quit()
    }

    @BeforeClass
    static void beforeClass() {
        // setup the ChromeDriver binary
        WebDriverManager.chromedriver().setup()
    }
}
