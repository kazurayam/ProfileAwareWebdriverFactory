package com.kazurayam.webdriverfactory

import com.beust.jcommander.JCommander
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.ChromeOptionsModifiers
import com.kazurayam.webdriverfactory.chrome.ChromePreferencesModifiers
import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import static org.junit.Assert.*

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
        WebDriverManager.chromedriver().clearDriverCache().setup()
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
        cookieServer.setPrintRequestRequired(true)
        cookieServer.setDebugMode(true)
        cookieServer.setCookieMaxAge(35)
        cookieServer.startup()
        ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
        cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
        LaunchedChromeDriver launched = cdf.newChromeDriver()
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
        ChromeDriverFactory cdf =
                ChromeDriverFactory.newChromeDriverFactory()
                        .addChromeOptionsModifier(ChromeOptionsModifiers.headless())
                        .addChromePreferencesModifier(ChromePreferencesModifiers.downloadWithoutPrompt())
                        .addChromePreferencesModifier(ChromePreferencesModifiers.downloadIntoDirectory(dir))
        LaunchedChromeDriver launched = cdf.newChromeDriver()
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
    void test_cli_print_request_positive() {
        BufferingRequestPrinter printer = new BufferingRequestPrinter()
        cookieServer.setRequestPrinter(printer)
        String[] argv = [ "--print-request" ]
        jc.parse(argv)
        assert cookieServer.isPrintRequestRequired
        cookieServer.setBaseDir(Paths.get("./src/web"))
        cookieServer.startup()
        //
        ChromeDriverFactory cdf = ChromeDriverFactory.newChromeDriverFactory()
        cdf.addChromeOptionsModifier(ChromeOptionsModifiers.headless())
        LaunchedChromeDriver launched = cdf.newChromeDriver()
        launched.getDriver().navigate().to("http://127.0.0.1:80/")
        Thread.sleep(3000)
        launched.getDriver().quit()
        String msg = printer.getMessage();
        println "msg: ${msg}"
        assertTrue(msg.contains("Accept-encoding"))
        //
        cookieServer.shutdown()
    }


    static class BufferingRequestPrinter implements CookieServer.RequestPrinter {
        StringBuilder sb = new StringBuilder();
        @Override
        void printRequest(String str) {
            sb.append(str)
        }
        String getMessage() {
            return sb.toString()
        }
    }


    @Test
    void test_cli_print_request_negative() {
        String[] argv = []
        jc.parse(argv)
        assert ! cookieServer.isPrintRequestRequired
    }

    @Test
    void test_cli_debug_positive() {
        String[] argv = [ "--debug" ]
        jc.parse(argv)
        assert cookieServer.isDebugMode
    }

    @Test
    void test_cli_debug_negative() {
        String[] argv = []
        jc.parse(argv)
        assert ! cookieServer.isDebugMode
    }
}
