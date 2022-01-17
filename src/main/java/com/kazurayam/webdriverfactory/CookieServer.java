package com.kazurayam.webdriverfactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A simple HTTP Server. This is useful to test my ChromeDriverFactory class.
 * It sends a Set-Cookie header for a cookie "timestamp" in the HTTP Response.
 * If the HTTP Request contained a cookie "timestamp", then the server will echo it back.
 *
 */
public class CookieServer {

    static final String URL_ENCODING = "UTF-8";
    static final String RESPONSE_ENCODING = "UTF-8";

    @Parameter(
            names = {"-p", "--port"},
            description = "port number. default : 80.",
            arity = 1)
    Integer port = 80;

    @Parameter(
            names = {"-b", "--base-dir"},
            description = "base directory path. default : current directory.",
            arity = 1)
    Path baseDir = Paths.get(".");

    @Parameter(
            names = {"-h", "--help"},
            help = true)
    boolean help;

    @Parameter(names = { "--print-request"},
            description = "display HTTP Request. default : true"
    )
    boolean isPrintingRequested = true;

    @Parameter(names = { "--debug"},
            description = "run with debug mode. default : false"
    )
    boolean isDebugMode = false;

    @Parameter(names = { "--cookieMaxAge" },
            description = "Max-Age of cookies. default : 60 (in seconds)",
            arity = 1)
    Integer cookieMaxAge = 60;

    private HttpServer httpServer;
    private ExecutorService httpThreadPool;
    private static final int NUM_OF_THREADS = 4;

    CookieServer() {}

    public CookieServer setPort(Integer port) {
        this.port = port;
        return this;
    }

    public CookieServer setBaseDir(Path baseDir) {
        this.baseDir = baseDir.toAbsolutePath().normalize();
        return this;
    }

    public CookieServer isPrintingRequested(boolean isPrintingRequested) {
        this.isPrintingRequested = isPrintingRequested;
        return this;
    }

    public CookieServer isDebugMode(boolean isDebugMode) {
        this.isDebugMode = isDebugMode;
        return this;
    }

    public CookieServer setCookieMaxAge(Integer cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
        return this;
    }

    public void startup() throws IOException {
        httpServer = HttpServer.create(
                new InetSocketAddress(port), 0);
        httpServer.createContext("/",
                new Handler(this.baseDir,
                        this.isDebugMode,
                        this.isPrintingRequested,
                        this.cookieMaxAge));
        httpThreadPool = Executors.newFixedThreadPool(NUM_OF_THREADS);
        httpServer.setExecutor(httpThreadPool);
        httpServer.start();
    }

    public void shutdown() {
        httpServer.stop(0);
        httpThreadPool.shutdown();
        try {
            httpThreadPool.awaitTermination(2, TimeUnit.HOURS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        CookieServer cookieServer = new CookieServer();
        JCommander jc = JCommander.newBuilder().addObject(cookieServer).build();
        jc.parse(args);
        if (cookieServer.help) {
            jc.usage();
        } else {
            cookieServer.startup();
        }
    }

    /**
     *
     */
    public static class Handler implements HttpHandler {
        private final Path baseDir;
        private final Boolean isDebugMode;
        private final Boolean isPrintingRequestRequired;
        private final Integer cookieMaxAge;
        private static final DateTimeFormatter RFC7231 =
                DateTimeFormatter
                        .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
                        .withZone(ZoneId.of("GMT"));

        Handler(Path baseDir, Boolean isDebugMode, Boolean isPrintingRequestRequired, Integer cookieMaxAge) {
            this.baseDir = baseDir;
            this.isDebugMode = isDebugMode;
            this.isPrintingRequestRequired = isPrintingRequestRequired;
            this.cookieMaxAge = cookieMaxAge;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                // accept the request
                if (isPrintingRequestRequired) {
                    printRequest(exchange);
                }
                String uri = exchange.getRequestURI().toString();
                String decodedUri = URLDecoder.decode(uri, URL_ENCODING);

                // do something special on cookies
                operateCookies(exchange);

                // build the response and send it back
                debugLog("\n>>>> Response sent");
                File file = new File(this.baseDir.toFile(), decodedUri);
                if (file.exists()) {
                    if (file.isFile()) {
                        debugLog(String.format("%s is a file", decodedUri));
                        writeFile(exchange, file);
                    } else if (file.isDirectory()) {
                        debugLog(String.format("%s is a directory.", decodedUri));
                        writeListOfFilesInDir(exchange, this.baseDir, file);
                    } else {
                        throw new IOException(String.format("%s is mysterious", file));
                    }
                } else {
                    debugLog(String.format("%s is not found.", file.getName()));
                    writeResponse(exchange, 404,
                            "<html><h1>Page Not Found</h1></html>");

                }
            } catch (Exception e) {
                e.printStackTrace();
                writeResponse(exchange, 500,
                        "<html><h1>Internal Server Error</h1></html>");
            }
        }

        private void printRequest(HttpExchange exchange) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n\n");
            sb.append("<<<< Request received\n");
            sb.append(String.format("method = %s\n", exchange.getRequestMethod()));
            sb.append(String.format("uri = %s\n", exchange.getRequestURI().toString()));
            sb.append(String.format("body = %s\n", stringifyInputStream(exchange.getRequestBody())));
            System.out.println(sb);
        }

        private String stringifyInputStream(InputStream inputStream) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return reader.lines().collect(Collectors.joining("\n"));
        }

        /**
         * copy cookies from the request to the response
         * if the request doesn't have "timestamp" cookie, add it
         */
        private void operateCookies(HttpExchange exchange) {
            boolean foundTimestampInRequest = false;
            List<String> cookieValues = new ArrayList<>();
            // copy the cookies from the request to the response
            Headers reqHeaders = exchange.getRequestHeaders();
            List<String> cookies = reqHeaders.get("Cookie");
            debugLog("==== Cookies cooked");
            if (cookies != null) {
                debugLog(String.format("in the request:  cookies=%s", cookies));
                for (String cookie : cookies) {
                    cookieValues.add(cookie + "; max-age=" + this.cookieMaxAge);
                    // check if the timestamp cookie is found
                    if (cookie.startsWith("timestamp")) {
                        foundTimestampInRequest = true;
                    }
                }
            } else {
                debugLog("in the request:  no cookies found");
            }
            // if the request has no "timestamp" cookie, create it into the response
            if (! foundTimestampInRequest) {
                ZonedDateTime now = ZonedDateTime.now();
                String timestampString =
                        String.format("timestamp=%s; Max-Age=%s;",
                                RFC7231.format(now), this.cookieMaxAge);
                cookieValues.add(timestampString);
            }
            Headers respHeaders = exchange.getResponseHeaders();
            respHeaders.put("Set-Cookie", cookieValues);
            debugLog(String.format("in the response: cookies=%s", cookieValues));
        }


        private void writeResponse(HttpExchange exchange, Integer rCode, String message)
                throws IOException
        {
            exchange.sendResponseHeaders(rCode, 0);
            OutputStream os = exchange.getResponseBody();
            OutputStreamWriter osw = new OutputStreamWriter(os, RESPONSE_ENCODING);
            osw.write(message);
            osw.flush();
            osw.close();
        }

        private void writeFile(HttpExchange exchange, File file) throws IOException {
            InputStream is = new FileInputStream(file);
            String contentType = ContentTypeResolver.resolve(file);
            exchange.getResponseHeaders().add("Content-Type", contentType);
            exchange.sendResponseHeaders(200, 0);
            OutputStream os = exchange.getResponseBody();
            copy(is, os);
            os.flush();
            os.close();
        }

        private void writeListOfFilesInDir(HttpExchange exchange, Path baseDir, File dir) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("<html><ul>\n");
            List<Path> sortedList = Files.list(dir.toPath())
                    .sorted((o1, o2) ->
                            o1.getFileName().toString().compareToIgnoreCase(o2.getFileName().toString()))
                    .collect(Collectors.toList());
            for (Path file : sortedList) {
                Path path = baseDir.relativize(file);
                try {
                    String encodedPath = URLEncoder.encode(path.toString(), URL_ENCODING);
                    sb.append(String.format("<li><a href=\"%s\">%s</a></li>\n",
                            encodedPath, file.getFileName()));
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
            sb.append("</ul></html>");
            this.writeResponse(exchange, 200, sb.toString());
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
