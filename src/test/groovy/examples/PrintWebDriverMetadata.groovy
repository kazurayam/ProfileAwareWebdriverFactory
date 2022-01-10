package examples


import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers
import com.kazurayam.webdriverfactory.chrome.ChromeUserProfile
import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
import com.kazurayam.webdriverfactory.chrome.ProfileDirectoryName
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
