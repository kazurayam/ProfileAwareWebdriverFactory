package com.kazurayam.webdriverfactory

import com.beust.jcommander.JCommander
import io.github.bonigarcia.wdm.WebDriverManager
import org.apache.hc.client5.http.auth.AuthScope
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.core5.http.HttpHost
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import static org.junit.Assert.*;

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Using Apache Http Client
 */
class CookieServerWithHttpClientTest {

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
    void test_printing_Authorization_header() {
        CookieServerTest.BufferingRequestPrinter printer =
                new CookieServerTest.BufferingRequestPrinter()
        cookieServer.setRequestPrinter(printer)
        String[] argv = [ "--print-request" ]
        jc.parse(argv)
        assert cookieServer.isPrintRequestRequired
        cookieServer.setBaseDir(Paths.get("./src/web"))
        cookieServer.startup()
        //
        HttpHost targetHost = new HttpHost("http", "localhost", 80)
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        AuthScope authScope = new AuthScope(targetHost);
        provider.setCredentials(authScope,
                new UsernamePasswordCredentials("mockUsername", "mockPassword".toCharArray()))
        HttpGet request = new HttpGet("http://localhost:80/");
        CloseableHttpClient client =
                HttpClientBuilder.create()
                        .setDefaultCredentialsProvider(provider)
                        .build()
        client.execute(request, { response ->
            int statusCode = response.getCode()
            assertEquals(200, statusCode)
        })
        String msg = printer.getMessage()
        println msg
        //
        cookieServer.shutdown()
    }


}
