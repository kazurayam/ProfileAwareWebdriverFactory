package study

import com.kazurayam.webdriverfactory.chrome.ChromeOptionsUtil
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.After;
import org.junit.Before
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.Proxy

/**
 * Study the following article by the ChromeDriver project
 * [Capabilities & ChromeOptions](https://chromedriver.chromium.org/capabilities)
 *
 * > Capabilities are options that you can use to customize and configure a ChromeDriver session. This page documents all ChromeDriver supported capabilities and how to use them.
 */
class ChromeOptionsTest {

    private String toJson(ChromeOptions options) {
        return ChromeOptionsUtil.toJson(options)
    }

    @Test(expected = IllegalArgumentException.class)
    void test_ChromeOptions_addExtensions() {
        ChromeOptions options = new ChromeOptions()
        options.addExtensions(new File("/path/to/extensions.crx"))
        driver = new ChromeDriver(options)
        println "test_ChromeOptions_assExtensions: " + toJson(options)
        driver.navigate().to("http://www.google.com")
        Thread.sleep(1000)
    }

    @Test
    void test_ChromeOptions_addArguments_windowSize() {
        ChromeOptions options = new ChromeOptions()
        options.addArguments("window-size=" + 800 + "," + 600)
        options.addArguments("--headless")
        driver = new ChromeDriver(options)
        println "test_ChromeOptions_addArguments_windowSize: " + toJson(options)
        driver.navigate().to("http://example.com")
        Thread.sleep(3000)
        driver.quit()
    };

    /**
     * Since Selenium version 3.6.0, the ChromeOptions class
     * in Java also implements the Capabilities interface,
     * allowing you to specify other WebDriver capabilities
     * not specific to ChromeDriver.
     */
    @Test
    void test_ChromeOptions_setCapability() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless")
        // Add the WebDriver proxy capability.
        Proxy proxy = new Proxy()
        proxy.setHttpProxy("myhttpproxy:3337")
        options.setCapability("proxy", proxy)
        println "test_ChromeOptions_setCapability: " + toJson(options)
        driver = new ChromeDriver(options);
        driver.quit()
    }

    /**
     * https://chromedriver.chromium.org/capabilities
     *
     * By default, ChromeDriver will create a new temporary profile for each session.
     * At times you may want to set special preferences or just use a custom profile altogether.
     * If the former, you can use the 'chrome.prefs' capability (described later below)
     * to specify preferences that will be applied after Chrome starts.
     * If the latter, you can use the user-data-dir Chrome command-line switch
     * to tell Chrome which profile to use:
     */

    private ChromeDriver driver;

    @BeforeClass
    static void beforeClass() {
        // setup the ChromeDriver binary
        WebDriverManager.chromedriver().setup()
    }

    @Before
    void setup() {
        driver = null
    }

    @After
    void tearDown() {
        if (driver != null) {
            driver.quit()
            driver = null
        }
    }

}
