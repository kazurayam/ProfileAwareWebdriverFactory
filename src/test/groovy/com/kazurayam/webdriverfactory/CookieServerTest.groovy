package com.kazurayam.webdriverfactory

import com.beust.jcommander.JCommander
import com.kazurayam.webdriverfactory.chrome.ChromeDriverFactory
import com.kazurayam.webdriverfactory.chrome.LaunchedChromeDriver
import org.junit.Before
import org.junit.Test

import java.nio.file.Path
import java.nio.file.Paths

class CookieServerTest {

    CookieServer cookieServer
    JCommander jc

    @Before
    void setup() {
        cookieServer = new CookieServer()
        jc = JCommander.newBuilder().addObject(cookieServer).build()
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
        Thread.sleep(55000)
        launched.getDriver().quit()
        cookieServer.shutdown()
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
    void test_cli_print_request() {
        String[] argv = [ "--print-request" ]
        jc.parse(argv)
        assert cookieServer.isPrintingRequested
    }

    @Test
    void test_cli_print_request_negative() {
        String[] argv = [ "--debug" ]
        jc.parse(argv)
        assert ! cookieServer.isPrintingRequested
    }

    @Test
    void test_cli_debug() {
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
