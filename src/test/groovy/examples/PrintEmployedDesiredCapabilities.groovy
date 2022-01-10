package examples


import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers
import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.openqa.selenium.remote.DesiredCapabilities

class PrintEmployedDesiredCapabilities {

    LaunchedChromeDriver launched

    @Test
    void test_getEmployedDesiredCapabilities() {
        ChromeDriverFactory factory = ChromeDriverFactory.newHeadlessChromeDriverFactory()
        factory.addChromeOptionsModifier(ChromeOptionsModifiers.incognito())
        launched = factory.newChromeDriver()
        launched.getEmployedDesiredCapabilities().ifPresent { DesiredCapabilities dc ->
            println dc
        }
        launched.getEmployedDesiredCapabilitiesAsJSON().ifPresent { String json ->
            println json
            assert json.contains("incognito")
        }
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
