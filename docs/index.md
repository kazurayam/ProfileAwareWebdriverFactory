# ChromeDriverFactory

&gt;can go back to <https://github.com/kazurayam/chromedriverfactory>

chromedriverfactory is a Java/Groovy library that wraps `org.openqa.selenium.webdriver.chrome.ChromeDriver`.
It enables you to launch Chrome browser while specifying a "User Profile", for example "Picasso". And also you can launch Chrome browser while specifying a "Profile Directory Name", for example "Default" or "Profile 1".

## Problem to solve

I want to use a User profile of Chrome browser, I want to save cookies in it.
I want to carry the cookie over into a HTTP session between my selenium test script and
Chrome browser. However it is difficult. Why? Have a look at a quotation from <https://chromedriver.chromium.org/capabilities>

> By default, ChromeDriver will create a new temporary profile for each session. At times you may want to set special preferences or just use a custom profile altogether. If the former, you can use the 'chrome.prefs' capability (described later below) to specify preferences that will be applied after Chrome starts. If the latter, you can use the user-data-dir Chrome command-line switch to tell Chrome which profile to use:

I want to break this default behavior.
I want to let ChromeDriver to use a profile which was prepared with contents as I wanted.

### What is "user profile", "user-data-dir" and "profile directory" in Chrome

What is "user profile" in Chrome browser? There are a few articles about it, for example

-   <https://www.guidingtech.com/things-about-google-chrome-profiles/>

## Solution description

I have developed a Java class `com.kazurayam.webdriverfactory.ChromeDriverFactory`.
With it, you can launch Chrome browser with "prepared User profile".

You have an option where to find the profile directory:

1.  `UserDataAccess.TO_GO` : ChromeDriverFactory allocates a new temporary directory and in there will create a profile directory. Then ChromeDriverFactory will copy the content of the genuine profile directory into the temporary directory. When you open Chrome with TO\_GO option the process will run. However, all of session data saved into this temporary profile directory will be discarded (cookies, cached page resources, etc) when you close the session.

2.  `UserDataAccess.FOR_HERE` : ChromeDriverFactory tries to find the profile directory in the Chrome’s genuine "user-data-dir". On Mac, it is `/Users/myosusername/Library/Application Support/Google/Chrome/`. A process of Chrome browser demands to lock the genuine profile directory. While a Chrome process is already running, when you try to open another Chrome with FOR\_HERE option, the new comer process will fail.

## Sequence diagrams

### UserDataAccess.TO\_GO

![sequence.TO GO](diagrams/out/sequence.TO_GO.png)

### UserDataAccess.FOR\_HERE

![sequence.FOR HERE](diagrams/out/sequence.FOR_HERE.png)

#### Caution

Chrome browser is very strict that one 1 process of Chrome can
have the access right to the genuine "UserData" folder. If you have
one Chrome window already opened when you try to launch one more Chrome process with `UserDataAccess.FOR_HERE`,
then that attempt will fail with a message:

    targetUserDataDir="/Users/username/Library/Application Support/Google/Chrome"
    profileDirectoryName="Profile 14"
    org.openqa.selenium.InvalidArgumentException was thrown.
    Exception message:

    invalid argument: user data directory is already in use, please specify a unique value for --user-data-dir argument, or don't use --user-data-dir

As countermeasures you have 2 options.

1.  close all Chrome windows before running your test, or

2.  your test uses `UserDataAccess.TO_GO` instead

## Sample codes

### Launch Chrome browser without profile specified

Basic case where we launch Chrome browser without profile specified. The example includes how to launch Headless browser. Also includes how to position and resize the browser window.

    package examples

    import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
    import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
    import io.github.bonigarcia.wdm.WebDriverManager

    import org.junit.After
    import org.junit.Before
    import org.junit.BeforeClass
    import org.junit.Test

    class LaunchChromeWithoutProfile {

        LaunchedChromeDriver launched

        @Test
        void test_launch_browser() {
            ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
            launched = factory.newChromeDriver()
            launched.getDriver().navigate().to("http://example.com")
            Thread.sleep(1000)
        }

        @Test
        void test_launch_headless_browser() {
            ChromeDriverFactory factory = ChromeDriverFactory.newHeadlessChromeDriverFactory()
            launched = factory.newChromeDriver()
            launched.getDriver().navigate().to("http://example.com")
            Thread.sleep(1000)
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

### Launch Chrome browser with UserProfile specified

    package examples

    import com.kazurayam.webdriverfactory.UserProfile
    import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
    import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
    import io.github.bonigarcia.wdm.WebDriverManager
    import org.junit.After
    import org.junit.Before
    import org.junit.BeforeClass
    import org.junit.Ignore
    import org.junit.Test

    class LaunchChromeWithUserProfile {

        LaunchedChromeDriver launched

        @Test
        void test_launch_browser_with_profile() {
            ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
            launched = factory.newChromeDriver(new UserProfile("Picasso"))
            launched.getDriver().navigate().to("http://example.com")
            Thread.sleep(1000)
        }

        @Test
        void test_launch_browser_with_profile_TO_GO() {
            ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
            launched = factory.newChromeDriver(
                    new UserProfile("Picasso"),
                    ChromeDriverFactory.UserDataAccess.TO_GO)
            launched.getDriver().navigate().to("http://example.com")
            Thread.sleep(1000)
        }

        @Ignore
        @Test
        void test_launch_browser_with_profile_FOR_HERE() {
            ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
            launched = factory.newChromeDriver(
                    new UserProfile("Picasso"),
                    ChromeDriverFactory.UserDataAccess.FOR_HERE)
            launched.getDriver().navigate().to("http://example.com")
            Thread.sleep(1000)
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

        /*
        @After
        void tearDown() {
            if (launched != null) {
                launched.getDriver().quit()
                launched = null
            }
        }
    */
    }

#### TO\_GO

When `UserDataAccess.TO_GO` is specified,
ChromeDriverFactory will let ChromeDriver to generate a commandline to start Chrome browser as follows for example on my Mac:

/Applications/Google Chrome.app/Contents/MacOS/Google Chrome --allow-pre-commit-input --disable-background-networking --disable-client-side-phishing-detection --disable-default-apps --disable-dev-shm-usage --disable-gpu --disable-hang-monitor --disable-infobars --disable-popup-blocking --disable-prompt-on-repost --disable-sync --disableExtensions --enable-automation --enable-blink-features=ShadowDOMV0 --enable-logging --log-level=0 --no-first-run --no-sandbox --no-service-autorun --password-store=basic --profile-directory=Profile 14 --remote-debugging-port=0 --test-type=webdriver --use-mock-keychain
--user-data-dir=/var/folders/7m/lm7d6nx51kj0kbtnsskz6r3m0000gn/T/*user-data-dir*7501750884155054730 --window-size=1024,768 --flag-switches-begin --flag-switches-end
---

Please note that the `--user-data-dir` option is given with a temporary directory.

#### FOR\_HERE

When `UserDataAccess.FOR_HERE` is specified,
ChromeDriverFactory will let ChromeDriver to generate a commandline to start Chrome browser as follows for example on my Mac:

    /Applications/Google Chrome.app/Contents/MacOS/Google Chrome --allow-pre-commit-input --disable-background-networking --disable-client-side-phishing-detection --disable-default-apps --disable-dev-shm-usage --disable-gpu --disable-hang-monitor --disable-infobars --disable-popup-blocking --disable-prompt-on-repost --disable-sync --disableExtensions --enable-automation --enable-blink-features=ShadowDOMV0 --enable-logging --log-level=0 --no-first-run --no-sandbox --no-service-autorun --password-store=basic --profile-directory=Profile 14 --remote-debugging-port=0 --test-type=webdriver --use-mock-keychain
        --user-data-dir=/Users/kazuakiurayama/Library/Application Support/Google/Chrome --window-size=1024,768 --flag-switches-begin --flag-switches-end

Please note that the `--user-data-dir` option is given with a path of "genuine User Data directory", which will be locked by a running Chrome process.

### Launch Headless Chrome with ProfileDirectoryName specified

    package examples

    import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
    import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
    import com.kazurayam.webdriverfactory.ProfileDirectoryName
    import io.github.bonigarcia.wdm.WebDriverManager
    import org.junit.After
    import org.junit.Before
    import org.junit.BeforeClass
    import org.junit.Ignore
    import org.junit.Test

    class LaunchChromeWithProfileDirectory {

        LaunchedChromeDriver launched

        @Test
        void test_launch_browser_with_profile_directory() {
            ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
            launched = factory.newChromeDriver(new ProfileDirectoryName("Profile 6"))
            launched.getDriver().navigate().to("http://example.com")
            Thread.sleep(1000)
        }

        @Test
        void test_launch_browser_with_profile_TO_GO() {
            ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
            launched = factory.newChromeDriver(
                    new ProfileDirectoryName("Profile 6"),
                    ChromeDriverFactory.UserDataAccess.TO_GO)
            launched.getDriver().navigate().to("http://example.com")
            Thread.sleep(1000)
        }

        @Ignore
        @Test
        void test_launch_browser_with_profile_FOR_HERE() {
            ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
            launched = factory.newChromeDriver(
                    new ProfileDirectoryName("Profile 6"),
                    ChromeDriverFactory.UserDataAccess.FOR_HERE)
            launched.getDriver().navigate().to("http://example.com")
            Thread.sleep(1000)
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

### print the DesiredCapabilities employed to open a Chrome

    package examples


    import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
    import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers
    import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
    import io.github.bonigarcia.wdm.WebDriverManager
    import org.junit.After
    import org.junit.Before
    import org.junit.BeforeClass
    import org.junit.Test
    import org.openqa.selenium.chrome.ChromeOptions

    class PrintEmployedOptions {

        LaunchedChromeDriver launched

        @Test
        void test_getEmployedOptions() {
            ChromeDriverFactory factory = ChromeDriverFactory.newHeadlessChromeDriverFactory()
            factory.addChromeOptionsModifier(ChromeOptionsModifiers.incognito())
            launched = factory.newChromeDriver()
            launched.getEmployedOptions().ifPresent { ChromeOptions options ->
                println options
            }
            launched.getEmployedOptionsAsJSON().ifPresent { String json ->
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

### print UserProfile, ProfileDirectoryName, UserDataDir used to launche a Chrome

    package examples


    import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
    import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers
    import com.kazurayam.webdriverfactory.chrome.ChromeUserProfile
    import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
    import com.kazurayam.webdriverfactory.ProfileDirectoryName
    import io.github.bonigarcia.wdm.WebDriverManager
    import org.junit.After
    import org.junit.Before
    import org.junit.BeforeClass
    import org.junit.Test

    import static org.junit.Assert.assertTrue

    class PrintWebDriverMetadata {

        LaunchedChromeDriver launched

        @Test
        void test_printUserProfile() {
            ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
            factory.addChromeOptionsModifier(ChromeOptionsModifiers.incognito())
            launched = factory.newChromeDriver(new ProfileDirectoryName("Default"),
                    ChromeDriverFactory.UserDataAccess.TO_GO)
            assertTrue(launched.getChromeUserProfile().isPresent())
            assertTrue(launched.getInstruction().isPresent())
            launched.getChromeUserProfile().ifPresent({ ChromeUserProfile cup ->
                println "ChromeUserProfile : " + cup
            })
            launched.getInstruction().ifPresent({ ChromeDriverFactory.UserDataAccess instruction ->
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

This emits

    ChromeUserProfile : {
        "userProfile": "kazurayam",
        "userDataDir": "/var/folders/lh/jkh513dn7f3c0j09z131g1z00000gn/T/__user-data-dir__7144377108201112266",
        "profileDirectoryName": "Default"
    }
    UserDataAccess: TO_GO

## References

-   [Using HTTP Cookies, MDN Web Docs](https://developer.mozilla.org/en-US/docs/Web/HTTP/Cookies)
