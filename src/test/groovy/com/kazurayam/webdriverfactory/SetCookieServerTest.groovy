package com.kazurayam.webdriverfactory

import com.beust.jcommander.JCommander
import org.junit.Before
import org.junit.Test

import java.nio.file.Path
import java.nio.file.Paths

class SetCookieServerTest {

    SetCookieServer stub
    JCommander jc

    @Before
    void setup() {
        stub = new SetCookieServer();
        jc = JCommander.newBuilder().addObject(stub).build();
    }

    @Test
    void test_cli_help() {
        String[] argv = [ "-h" ]
        jc.parse(argv)
        if (stub.help) {
            jc.usage()
        }
    }

    @Test
    void test_cli_port() {
        String[] argv = [ "-p", "8080" ]
        jc.parse(argv);
        assert stub.port == 8080
    }

    @Test
    void test_cli_port_long() {
        String[] argv = [ "--port", "8080" ]
        jc.parse(argv);
        assert stub.port == 8080
    }

    @Test
    void test_cli_baseDir() {
        Path p = Paths.get(".").resolve("src/web").toAbsolutePath().normalize()
        String[] argv = [ "-b", p.toString() ]
        jc.parse(argv)
        println stub.baseDir
        assert stub.baseDir.toString().contains("web")
    }

    @Test
    void test_cli_print_request() {
        String[] argv = [ "--print-request" ]
        jc.parse(argv)
        assert stub.isPrintingRequested
    }

    @Test
    void test_cli_print_request_negative() {
        String[] argv = [ "--debug" ]
        jc.parse(argv)
        assert ! stub.isPrintingRequested
    }

    @Test
    void test_cli_debug() {
        String[] argv = [ "--debug" ]
        jc.parse(argv)
        assert stub.isDebugMode
    }

    @Test
    void test_cli_debug_negative() {
        String[] argv = [ "--print-request" ]
        jc.parse(argv)
        assert ! stub.isDebugMode
    }
}
