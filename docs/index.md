# webdriverfactory

A Java/Groovy library that wraps `org.openqa.selenium.webdriver.chrome.ChromeDriver`.
It enables you to launch Chrome browser while specifying a **Profile** so that cookies and cached page resources are carried over from previous HTTP session to the next.

## Examples

### Launch Chrome browser without profile specified

Basic case where we launch Chrome browser without profile specified. The example includes how to launch Headless brower. Also includes how to position and resize the browser window.

    package examples

    import com.kazurayam.browserwindowlayout.BrowserWindowLayoutManager
    import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
    import io.github.bonigarcia.wdm.WebDriverManager

    import org.junit.After
    import org.junit.Before
    import org.junit.BeforeClass
    import org.junit.Test
    import org.openqa.selenium.Dimension
    import org.openqa.selenium.Point
    import org.openqa.selenium.chrome.ChromeDriver

    class LaunchChromeWithoutProfile {

        ChromeDriver browser

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

        @Test
        void test_launch_browser() {
            ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
            browser = factory.newChromeDriver()
            browser.navigate().to("http://example.com")
            Thread.sleep(1000)
        }

        @Test
        void test_launch_headless_browser() {
            ChromeDriverFactory factory = ChromeDriverFactory.newHeadlessChromeDriverFactory()
            browser = factory.newChromeDriver()
            browser.navigate().to("http://example.com")
            Thread.sleep(1000)
        }

        @Test
        void test_launch_and_layout_browser() {
            ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
            browser = factory.newChromeDriver()
            Point position = new Point(100, 100)
            Dimension dimension = new Dimension(800, 600)
            BrowserWindowLayoutManager.layout(browser, position, dimension)
            browser.navigate().to("http://example.com")
            Thread.sleep(1000)
        }
    }

### Launch Chrome browser with UserProfile specified

    package examples

    import com.kazurayam.webdriverfactory.UserProfile
    import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
    import io.github.bonigarcia.wdm.WebDriverManager
    import org.junit.After
    import org.junit.Before
    import org.junit.BeforeClass
    import org.junit.Ignore
    import org.junit.Test
    import org.openqa.selenium.chrome.ChromeDriver

    class LaunchChromeWithUserProfile {

        ChromeDriver browser

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

        @Test
        void test_launch_browser_with_profile() {
            ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
            browser = factory.newChromeDriver(new UserProfile("Picasso"))
            browser.navigate().to("http://example.com")
            Thread.sleep(1000)
        }

        @Test
        void test_launch_browser_with_profile_TO_GO() {
            ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
            browser = factory.newChromeDriver(
                    new UserProfile("Picasso"),
                    ChromeDriverFactory.UserDataAccess.TO_GO)
            browser.navigate().to("http://example.com")
            Thread.sleep(1000)
        }

        @Ignore
        @Test
        void test_launch_browser_with_profile_FOR_HERE() {
            ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
            browser = factory.newChromeDriver(
                    new UserProfile("Picasso"),
                    ChromeDriverFactory.UserDataAccess.FOR_HERE)
            browser.navigate().to("http://example.com")
            Thread.sleep(1000)
        }
    }

### Launch Headless Chrome with ProfileDirectoryName specified

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
                    new ProfileDirectoryName("Default"),
                    ChromeDriverFactory.UserDataAccess.FOR_HERE)
            browser.navigate().to("http://example.com")
            Thread.sleep(1000)
        }

    }

**STILL TO BE AUTHORED**

[Open Browser with Custom Profile](https://forum.katalon.com/t/open-browser-with-custom-profile/19268)
