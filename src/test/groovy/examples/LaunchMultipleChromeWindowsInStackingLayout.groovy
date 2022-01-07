package examples

import com.kazurayam.browserwindowlayout.BrowserWindowLayoutManager
import com.kazurayam.browserwindowlayout.StackingWindowLayoutMetrics
import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.Dimension
import org.openqa.selenium.Point
import org.openqa.selenium.chrome.ChromeDriver

class LaunchMultipleChromeWindowsInStackingLayout {

    @Test
    void test_open2windows_in_stacking_layout() {
        StackingWindowLayoutMetrics layoutMetrics =
                new StackingWindowLayoutMetrics.Builder(2)
                        .windowDimension(new Dimension(1000, 600))
                        .disposition(new Point(400, 200))
                        .build()
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        ChromeDriver browser0 = factory.newChromeDriver(new UserProfile("Picasso"))
        BrowserWindowLayoutManager.layout(browser0,
                layoutMetrics.getWindowPosition(0),
                layoutMetrics.getWindowDimension(0))
        ChromeDriver browser1 = factory.newChromeDriver(new UserProfile("Gogh"))
        BrowserWindowLayoutManager.layout(browser1,
                layoutMetrics.getWindowPosition(1),
                layoutMetrics.getWindowDimension(1))
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

}
