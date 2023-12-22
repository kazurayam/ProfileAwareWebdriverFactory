package com.kazurayam.webdriverfactory.edge

import com.kazurayam.webdriverfactory.CookieServer
import com.kazurayam.webdriverfactory.ProfileDirectoryName
import com.kazurayam.webdriverfactory.UserDataAccess
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.*

import java.nio.file.Files;
import java.nio.file.Path
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull

class EdgeDriverFactoryTest {

    static Path outputFolder
    LaunchedEdgeDriver launched
    static CookieServer cookieServer

    @BeforeClass
    static void beforeClass() {
        WebDriverManager.edgedriver().setup()
        outputFolder = Paths.get(".").resolve("build/tmp/testOutput")
                .resolve(EdgeDriverFactoryTest.class.getSimpleName())
        Files.createDirectories(outputFolder)
        //
        cookieServer = new CookieServer()
        cookieServer.setBaseDir(Paths.get("./src/web"))
        cookieServer.isPrintingRequested(true)
        cookieServer.isDebugMode(true)
        cookieServer.startup()
    }

    @Before
    void setup() {
        launched = null
    }

    @After
    void tearDown() {
        if (launched != null) {
            launched.getDriver().quit()
            launched = null
        }
    }

    @AfterClass
    static void afterClass() {
        cookieServer.shutdown()
    }

    @Test
    void test_newEdgeDriver_byProfileDirectoryName_TO_GO() {
        EdgeDriverFactory edFactory = EdgeDriverFactory.newEdgeDriverFactory()
        launched = edFactory.newEdgeDriver(
                new ProfileDirectoryName('Profile 1'),
                UserDataAccess.TO_GO)  // or 'Default'
        assertNotNull(launched)

        //println("ChromeDriver has been instantiated with profile directory Default")
        launched.getDriver().navigate().to('http://example.com/')
    }
}
