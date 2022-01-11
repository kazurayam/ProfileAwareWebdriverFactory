# webdriverfactory

&gt;can go back to <https://github.com/kazurayam/webdriverfactory>

webdriverfactory is a Java/Groovy library that wraps `org.openqa.selenium.webdriver.chrome.ChromeDriver`.
It enables you to launch Chrome browser while specifying a "User Profile", for example "Picasso". And also you can lauch Chrome browser while specifying a "Profile Directory Name", for example "Default" or "Profile 1".

## Sample codes

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

### Launch Headless Chrome with ProfileDirectoryName specified

    package examples

    import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
    import com.kazurayam.webdriverfactory.ProfileDirectoryName
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

### Launch Multiple Chrome windows in Tiling layout

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

### Launch Multiple Chrome windows in Stacking layout

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

### print the DesiredCapabilities employed to open a Chrome

    package examples

    import com.kazurayam.webdriverfactory.UserProfile
    import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
    import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers
    import com.kazurayam.webdriverfactory.ProfileDirectoryName
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

This emits:

    {
        "acceptSslCerts": true,
        "browserName": "chrome",
        "goog:chromeOptions": {
            "args": [
                "window-size=1024,768",
                "--no-sandbox",
                "disable-infobars",
                "disable-gpu",
                "disable-dev-shm-usage",
                "--incognito"
            ],
            "extensions": [

            ],
            "prefs": {
                "plugins.plugins_disabled": [
                    "Adobe Flash Player",
                    "Chrome PDF Viewer"
                ],
                "profile.default_content_settings.popups": 0,
                "download.prompt_for_download": false,
                "download.default_directory": "/Users/kazuakiurayama/Downloads"
            }
        }
    }

### print UserProfile, ProfieDirectoryName, UserDataDir used to launche a Chrome

    package examples

    import com.kazurayam.webdriverfactory.UserProfile
    import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
    import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers
    import com.kazurayam.webdriverfactory.chrome.ChromeUserProfile
    import com.kazurayam.webdriverfactory.ProfileDirectoryName
    import io.github.bonigarcia.wdm.WebDriverManager
    import org.junit.After
    import org.junit.Before
    import org.junit.BeforeClass
    import org.junit.Test
    import org.openqa.selenium.chrome.ChromeDriver
    import org.openqa.selenium.remote.DesiredCapabilities

    import java.nio.file.Files

    import static org.junit.Assert.assertEquals
    import static org.junit.Assert.assertEquals
    import static org.junit.Assert.assertTrue
    import static org.junit.Assert.assertTrue

    class PrintWebDriverMetadata {

        ChromeDriver browser

        @Test
        void test_printUserProfile() {
            ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
            factory.addChromeOptionsModifier(ChromeOptionsModifiers.incognito())
            browser = factory.newChromeDriver(new ProfileDirectoryName("Default"),
                    ChromeDriverFactory.UserDataAccess.TO_GO)
            assertTrue(browser.userProfile.isPresent())
            assertTrue(browser.userDataAccess.isPresent())
            browser.userProfile.ifPresent({ ChromeUserProfile cup ->
                println "ChromeUserProfile : " + cup
            })
            browser.userDataAccess.ifPresent({ ChromeDriverFactory.UserDataAccess instruction ->
                println "UserDataAccess: " + instruction
            })
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

This emits

    ChromeUserProfile : {
        "userProfile": "kazurayam",
        "userDataDir": "/var/folders/lh/jkh513dn7f3c0j09z131g1z00000gn/T/__user-data-dir__7144377108201112266",
        "profileDirectoryName": "Default"
    }
    UserDataAccess: TO_GO

## What is "user profile", "user-data-dir" and "profile directory" in Chrome

What is "user profile" in Chrome browser? There are a few articles about it, for example

-   <https://www.guidingtech.com/things-about-google-chrome-profiles/>

## Problem to solve

## Solution

## Description

You have an option where to find the profile directory:

1.  `UserDataAccess.FOR_HERE` : ChromeDriverFactory tries to find the profile directory in the Chrome’s genuine "user-data-dir". On Mac, it is `/Users/myosusername/Library/Application Support/Google/Chrome/`. A process of Chrome browser demands to lock the genuine profile directory. While a Chrome process is already running, when you try to open another Chrome with FOR\_HERE option, the new comer process will fail.

2.  `UserDataAccess.TO_GO` : ChromeDriverFactory allocates a new temporary directory and in there will create a profile directory. Then ChromeDriverFactory will copy the content of the genuine profile directory into the temporary directory. When you open Chrome with TO\_GO option the process will run. However, all of session data saved into this temporary profile directory will be discarded (cookies, cached page resources, etc) when you close the session.
