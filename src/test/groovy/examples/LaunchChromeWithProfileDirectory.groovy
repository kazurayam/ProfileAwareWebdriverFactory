package examples

import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.ProfileDirectoryName
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test
import org.openqa.selenium.chrome.ChromeDriver

class LaunchChromeWithProfileDirectory {

    ChromeDriver browser

    @Test
    void test_launch_browser_with_profile_directory() {
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        browser = factory.newChromeDriver(new ProfileDirectoryName("Profile 6"))
        browser.navigate().to("http://example.com")
        Thread.sleep(1000)
    }

    @Test
    void test_launch_browser_with_profile_TO_GO() {
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        browser = factory.newChromeDriver(
                new ProfileDirectoryName("Profile 6"),
                ChromeDriverFactory.UserDataAccess.TO_GO)
        browser.navigate().to("http://example.com")
        Thread.sleep(1000)
    }

    @Ignore
    @Test
    void test_launch_browser_with_profile_FOR_HERE() {
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        browser = factory.newChromeDriver(
                new ProfileDirectoryName("Profile 6"),
                ChromeDriverFactory.UserDataAccess.FOR_HERE)
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
