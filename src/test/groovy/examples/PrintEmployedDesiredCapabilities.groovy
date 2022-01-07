package examples

import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers
import com.kazurayam.webdriverfactory.chrome.ProfileDirectoryName
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.remote.DesiredCapabilities

class PrintEmployedDesiredCapabilities {

    ChromeDriver browser

    @Test
    void test_getEmployedDesiredCapabilities() {
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        factory.addChromeOptionsModifier(ChromeOptionsModifiers.incognito())
        browser = factory.newChromeDriver()
        DesiredCapabilities dc = factory.getEmployedDesiredCapabilities()
        assert dc != null
        String str = factory.getEmployedDesiredCapabilitiesAsJSON()
        assert str != null
        println str
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
