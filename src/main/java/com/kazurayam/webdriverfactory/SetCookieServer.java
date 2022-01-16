package com.kazurayam.webdriverfactory;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;

import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple HTTP Server. This is usefule to test my ChromeDriverFactory class.
 * It sends a Set-Cookie header for a cookie "timestamp" in the HTTP Response.
 * If the HTTP Request contained a cookie "timestamp", then the server will echoes it back.
 *
 */
public class SetCookieServer {

    static final String URL_ENCODING = "UTF-8";
    static final String RESPONSE_ENCODING = "UTF-8";

    private Integer port;

    SetCookieServer() {
    }

    public void run() throws IOException {
        HttpServer server = HttpServer.create(
                new InetSocketAddress(port), 0);
        server.createContext("/",
                new Handler(80,
                        Paths.get("."),
                        true,
                        true));
        server.start();
    }

    public void shutdown() {
        throw new RuntimeException("TODO");
    }

    public static void main(String[] args) throws IOException {
        SetCookieServer server = new SetCookieServer();
        server.run();
    }

    /**
     *
     */
    public static class Handler implements HttpHandler {
        private Integer port;
        private Path basePath;
        private Boolean isDebugMode;
        private Boolean isPrintingRequestRequired;

        private static final Long MAX_AGE_SECONDS = 60L;
        private static final DateTimeFormatter RFC7231 =
                DateTimeFormatter
                        .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
                        .withZone(ZoneId.of("GMT"));

        Handler(Integer port, Path basePath, Boolean isDebugMode, Boolean isPrintingRequestRequired) {
            this.port = port;
            this.basePath = Paths.get(".");
            this.isDebugMode = true;
            this.isPrintingRequestRequired = true;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // accept the request
                printRequest(exchange);
                String uri = exchange.getRequestURI().toString();
                String decodedUri = URLDecoder.decode(uri, URL_ENCODING);

                // do something special on cookies
                operateCookies(exchange);

                // build the response and send it back
                File file = new File(basePath.toFile(), decodedUri);
                if (file.exists()) {
                    if (file.isFile()) {
                        debugLog(String.format("%s is a file", file.getName()));
                        writeFile(exchange, file);
                    } else if (file.isDirectory()) {

                    } else {
                        throw new IOException(String.format("%s is mysterious", file.toString()));
                    }
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
                this.sendResponse(exchange, 500,
                        "<html><h1>Internal Server Error</h1></html>");
            }
        }

        private void printRequest(HttpExchange exchange) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n\n\n\n");
            sb.append(">>>>\n");
            sb.append(String.format("method = %s\n", exchange.getRequestMethod()));
            sb.append(String.format("uri = %s\n", exchange.getRequestURI()));
            sb.append(String.format("body = %s\n", exchange.getRequestBody()));
            System.out.println(sb.toString());
        }

        /**
         * copy cookies from the request to the response
         * if the request doesn't have "timestamp" cookie, add it
         */
        private void operateCookies(HttpExchange exchange) {
            // copy the cookies from the request to the response
            Headers reqHeaders = exchange.getRequestHeaders();
            List<String> cookies = reqHeaders.get("Cookie");
            this.debugLog(String.format("request:  cookies=%s", cookies));
            List<String> values = new ArrayList<>();
            boolean foundTimestamp = false;
            for (String cookie : cookies) {
                values.add(cookie + "; max-age=" + MAX_AGE_SECONDS);
                // check if the timestamp cookie is found
                if (cookie.startsWith("timestamp")) {
                    foundTimestamp = true;
                }
            }
            // if the request has no "timestamp" cookie, create it into the response
            if (! foundTimestamp) {
                ZonedDateTime now = ZonedDateTime.now();
                String timestampString = "timestamp=" + RFC7231.format(now) + "; " +
                        "Max-Age=" + MAX_AGE_SECONDS + ";";
                values.add(timestampString);
            }
            Headers respHeaders = exchange.getResponseHeaders();
            respHeaders.put("Set-Cookie", values);
            this.debugLog(String.format("response: cookies=%s", values));
        }


        private void sendResponse(HttpExchange exchange, Integer rCode, String message)
                throws IOException
        {
            exchange.sendResponseHeaders(rCode, 0);
            OutputStream os = exchange.getResponseBody();
            OutputStreamWriter osw = new OutputStreamWriter(os, RESPONSE_ENCODING);
            osw.write(message);
            osw.flush();
        }

        private void writeFile(HttpExchange exchange, File file) throws IOException {
            InputStream is = new FileInputStream(file);
            String contentType = ContentTypeResolver.resolve(file);
            exchange.getResponseHeaders().add("Content-Type", contentType);
            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            copy(is, os);
            os.flush();
        }

        private static void copy(InputStream source, OutputStream target) throws IOException {
            byte[] buf = new byte[8192];
            int length;
            while ((length = source.read(buf)) > 0) {
                target.write(buf, 0, length);
            }
        }

        private void debugLog(String message) {
            if (this.isDebugMode) {
                System.out.println(message);
            }
        }
    }


    /**
     *
     */
    public static class ContentTypeResolver {

        public static String resolve(File file) {
            String extension = getExtension(file);
            return CONTENT_MAP.getOrDefault(extension, DEFAULT_CONTENT_TYPE);
        }

        private static String getExtension(File file) {
            int dotIndex = file.getName().lastIndexOf(".");
            if (dotIndex == -1) {
                return "";
            } else {
                return file.getName().substring(dotIndex + 1);
            }
        }

        private static final String DEFAULT_CONTENT_TYPE = "text/plain";

        private static final Map<String, String> CONTENT_MAP =
                new HashMap<String, String>() {
                    {
                        put("html", "text/html");
                        put("jpg", "image/jpeg");
                        put("jpeg", "image/jpeg");
                        put("png", "image/png");
                        put("gif", "image/gif");
                        put("pdf", "application/pdf");
                        put("xls", "application/octet-stream");
                        put("xlsx", "application/octet-stream");
                        put("doc", "application/octet-stream");
                        put("docx", "application/octet-stream");
                        put("js", "application/javascript");
                        put("json", "application/javascript");
                        put("css", "text/css");
                        put("xml", "application/xml");
                        // append mapping entry if you need.
                    }};
    }


}
