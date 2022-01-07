package examples

import com.kazurayam.webdriverfactory.UserProfile
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers
import com.kazurayam.webdriverfactory.chrome.ChromeUserProfile
import com.kazurayam.webdriverfactory.chrome.ProfileDirectoryName
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
