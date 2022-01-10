import java.net.InetSocketAddress
import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.file.Path
import java.nio.file.Paths
import java.time.ZonedDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

def cli = new CliBuilder(usage: 'http <option>')

cli.with {
    p longOpt: 'port', args: 1, argName: 'port', 'port number. default : 80.'
    b longOpt: 'base-dir', args: 1, argName: 'path', 'base directory path. default : current directory.'
    h longOpt: 'help', 'show this help.'
    _ longOpt: 'print-request', 'display request infomation.'
    _ longOpt: 'debug', 'run with debug mode.'
}

def cmd = cli.parse(args)

if (!cmd) return

if (cmd.h) {
    cli.usage()
    return
}

def port = (cmd.p ?: 80) as int
def basePath = Paths.get(cmd.b ?: '.')
def debug = cmd.debug
def printRequest = cmd.'print-request'

println """
port=${port}
base-dir=${basePath.toAbsolutePath()}
"""

server = HttpServer.create(new InetSocketAddress(port), 0)
server.createContext("/", new Handler(debug: debug, basePath: basePath, printRequest: printRequest))
server.start()

class Handler implements HttpHandler {
    static final String URL_ENCODING = 'UTF-8'
    static final String RESPONSE_ENCODING = 'UTF-8'
    Path basePath
    boolean debug
    boolean printRequest

    @Override
    void handle(HttpExchange exchange) {
        try {
            //
            this.printRequestInfo(exchange)
            def uri = exchange.requestURI.toString()
            def decodedUri = URLDecoder.decode(uri, URL_ENCODING)
            this.debugLog {"uri=${uri}, decodedUri=${decodedUri}"}

            // copy all cookies in the request into the response.
            // add "timestamp" cookie if not there
            // the cookies in the response will have Max-Age=60 (1minutes)
            operateCookies(exchange)

            // now we build the response body and send response back
            File file = new File(basePath.toFile(), decodedUri)
            if (file.exists()) {
                if (file.isFile()) {
                    this.debugLog {"${file.name} is file."}
                    this.writeFile(exchange, file)
                } else if (file.isDirectory()) {
                    this.debugLog {"${file.name} is directory."}
                    this.writeFileListInDir(exchange, file)
                }
            } else {
                this.debugLog {"${file.name} is not found."}
                this.response(exchange, 404, '<html><h1>Page Not Found</h1></html>')
            }
        } catch (e) {
            e.printStackTrace()
            this.response(exchange, 500, '<html><h1>Internal Server Error</h1></html>')
        }
    }

    private operateCookies(exchange) {
      // copy cookies from the request to the response
      // if the request doesn't have "timestamp" cookie, add it
      Headers reqHeaders = exchange.getRequestHeaders()
      List<String> cookies = reqHeaders.get("Cookie")
      this.debugLog {"request: cookies=${cookies}"}
      List<String> values = new ArrayList<>()
      long maxAgeSeconds = 60L;
      boolean foundTimestamp = false
      for (String cookie in cookies) {
        values.add(cookie + "; max-age=" + maxAgeSeconds);
        if (cookie.startsWith("timestamp")) {
          foundTimestamp = true
        }
      }
      // if there is no "timestamp" cookie, create it
      if (! foundTimestamp) {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter rfc7231 = DateTimeFormatter
            .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
            .withZone(ZoneId.of("GMT"))
        String timestampString = "timestamp=" + rfc7231.format(now) + "; " + 
            "Max-Age=" + maxAgeSeconds + ";";
        values.add(timestampString);
      }
      Headers respHeaders = exchange.getResponseHeaders()
      respHeaders.put("Set-Cookie", values)
      this.debugLog {"response: cookies=${values}"}
    }



    private void printRequestInfo(exchange) {
        println """\


>>>>
method = ${exchange.requestMethod}
uri = ${exchange.requestURI}
body = ${exchange.requestBody}
"""
    }

    private void writeFile(exchange, file) {
        this.setContentType(exchange, file)
        exchange.sendResponseHeaders(200, 0)
        exchange.responseBody.withStream { it.write(file.bytes) }
    }

    private void writeFileListInDir(exchange, dir) {
        def sb = new StringBuilder()
        sb << '<html><ul>'

        dir.listFiles().sort{ it.name }.each { file ->
            def path = this.basePath.relativize(file.toPath()).toString().replace('\\', '/')
            def encodedPath = URLEncoder.encode(path, URL_ENCODING)

            this.debugLog {" - name:${file.name}, filePath:${file.path}, path=${path}, encodedPath=${encodedPath}"}

            sb << "<li><a href=\"${encodedPath}\">${file.name}</a></li>"
        }

        sb << '</ul></html>'

        this.response(exchange, 200, sb.toString())
    }

    private void response(exchange, sc, message) {
        exchange.sendResponseHeaders(sc, 0)
        exchange.responseBody.withWriter(RESPONSE_ENCODING) {it.write(message)}
    }

    private void debugLog(message) {
        if (this.debug) println message()
    }

    private void setContentType(exchange, file) {
        def extension = this.getExtension(file)
        this.debugLog {"extention = ${extension}"}
        def contentType = CONTENT_MAP[extension] ?: DEFAULT_CONTENT_TYPE
        this.debugLog {"contentType = ${contentType}"}

        exchange.responseHeaders.add('Content-Type', contentType)
    }

    private String getExtension(file) {
        int dotIndex = file.name.lastIndexOf('.')

        if (dotIndex == -1) {
            null
        } else {
            file.name.substring(dotIndex + 1)
        }
    }

    private static final String DEFAULT_CONTENT_TYPE = 'text/plain';

    private static final def CONTENT_MAP = [
        'html': 'text/html',
        'jpg': 'image/jpeg',
        'jpeg': 'image/jpeg',
        'png': 'image/png',
        'gif': 'image/gif',
        'pdf': 'application/pdf',
        'xls': 'application/octet-stream',
        'xlsx': 'application/octet-stream',
        'doc': 'application/octet-stream',
        'docx': 'application/octet-stream',
        'js': 'application/javascript',
        'json': 'application/javascript',
        'css': 'text/css',
        'xml': 'application/xml',
        // append mapping entry if you need.
    ];
}
