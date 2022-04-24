package com.kazurayam.webdriverfactory

import com.beust.jcommander.JCommander
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactoryImpl
import com.kazurayam.webdriverfactory.chrome.ChromePreferencesModifiers
import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class CookieServerTest {

    CookieServer cookieServer
    JCommander jc

    Path outputDir

    @BeforeClass
    static void beforeClass() {
        // setup the ChromeDriver binary
        WebDriverManager.chromedriver().setup()
    }

    @Before
    void setup() {
        cookieServer = new CookieServer()
        jc = JCommander.newBuilder().addObject(cookieServer).build()
        outputDir = Paths.get("./build/tmp/testOutput")
                .resolve(CookieServerTest.class.getSimpleName())
        Files.createDirectories(outputDir)
    }

    @Test
    void test_startup_shutdown() {
        cookieServer.setPort(8080)
        cookieServer.setBaseDir(Paths.get("./src/web"))
        cookieServer.isPrintingRequested(true)
        cookieServer.isDebugMode(true)
        cookieServer.setCookieMaxAge(35)
        cookieServer.startup()
        ChromeDriverFactory factory = ChromeDriverFactory.newChromeDriverFactory()
        LaunchedChromeDriver launched = factory.newChromeDriver()
        launched.getDriver().navigate().to("http://127.0.0.1:8080/")
        Thread.sleep(3000)
        launched.getDriver().quit()
        cookieServer.shutdown()
    }

    /**
     * test if a large file is downloaded quick enough
     */
    @Test
    void test_downloading_a_large_file() {
        cookieServer.setBaseDir(Paths.get("./src/web"))
        cookieServer.startup()
        //
        Path dir = outputDir.resolve("test_downloading_a_large_file")
        if (Files.exists(dir)) {
            dir.toFile().deleteDir()
        }
        Files.createDirectories(dir)
        ChromeDriverFactory factory =
                ChromeDriverFactory.newChromeDriverFactory()
                .addChromePreferencesModifier(
                        ChromePreferencesModifiers.downloadWithoutPrompt())
                .addChromePreferencesModifier(
                        ChromePreferencesModifiers.downloadIntoDirectory(dir))
        LaunchedChromeDriver launched = factory.newChromeDriver()
        launched.getDriver().navigate().to("http://127.0.0.1/SDG_DSD_MATRIX.1.7.xlsm")
        Thread.sleep(3000)   // expect the downloading to finish in 3 seconds
        launched.getDriver().quit()
        //
        cookieServer.shutdown()
        //
        Path downloaded = dir.resolve("SDG_DSD_MATRIX.1.7.xlsm");
        assert Files.exists(downloaded)
    }

    @Test
    void test_cli_help() {
        String[] argv = [ "-h" ]
        jc.parse(argv)
        if (cookieServer.help) {
            jc.usage()
        }
    }

    @Test
    void test_cli_port() {
        String[] argv = [ "-p", "8080" ]
        jc.parse(argv)
        assert cookieServer.port == 8080
    }

    @Test
    void test_cli_port_long() {
        String[] argv = [ "--port", "8080" ]
        jc.parse(argv)
        assert cookieServer.port == 8080
    }

    @Test
    void test_cli_baseDir() {
        Path p = Paths.get(".").resolve("src/web").toAbsolutePath().normalize()
        String[] argv = [ "-b", p.toString() ]
        jc.parse(argv)
        println cookieServer.baseDir
        assert cookieServer.baseDir.toString().contains("web")
    }

    @Test
    void test_cli_print_request_negative() {
        String[] argv = [ "--print-request" ]
        jc.parse(argv)
        assert ! cookieServer.isPrintingRequested
    }

    @Test
    void test_cli_print_request_positive() {
        String[] argv = [ "--debug" ]
        jc.parse(argv)
        assert cookieServer.isPrintingRequested
    }

    @Test
    void test_cli_debug_positive() {
        String[] argv = [ "--debug" ]
        jc.parse(argv)
        assert cookieServer.isDebugMode
    }

    @Test
    void test_cli_debug_negative() {
        String[] argv = [ "--print-request" ]
        jc.parse(argv)
        assert ! cookieServer.isDebugMode
    }
}
