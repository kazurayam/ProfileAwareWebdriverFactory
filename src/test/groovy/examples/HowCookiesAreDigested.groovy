package examples

import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.ProfileDirectoryName
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.chrome.ChromeDriver

class HowCookiesAreDigested {

    ChromeDriver browser

    @Test
    void test_launch_browser_with_profile_TO_GO() {
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        browser = factory.newChromeDriver(
                new UserProfile("Kazuaki"),
                ChromeDriverFactory.UserDataAccess.TO_GO)
        browser.navigate().to("https://mail.google.com/mail/u/0/#inbox")
        Thread.sleep(1000)
    }

    @Test
    void test_launch_browser_with_profiledirectoryname_TO_GO() {
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        browser = factory.newChromeDriver(
                new ProfileDirectoryName("Default"),
                ChromeDriverFactory.UserDataAccess.TO_GO)
        browser.navigate().to("https://mail.google.com/mail/u/0/#inbox")
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

    /*
    @After
    void tearDown() {
        if (browser != null) {
            browser.quit()
            browser = null
        }
    }
     */

}
