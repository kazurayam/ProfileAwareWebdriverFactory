package com.kazurayam.webdriverfactory.utils

import org.openqa.selenium.Cookie

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CookieClassHelper {

    static final DateTimeFormatter rfc7231 = DateTimeFormatter
            .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)
            .withZone(ZoneId.of("GMT"))

    static void overrideToString() {
        Cookie.metaClass.toString = { ->
            Cookie ck = delegate
            StringBuilder sb = new StringBuilder()
            sb.append(ck.getName())
            sb.append("=")
            if (ck.getValue() != null) {
                sb.append(ck.value)
            }
            Date expires = ck.getExpiry()
            if (expires != null) {
                sb.append("; expires=")
                ZonedDateTime zdt = expires.toInstant().atZone(ZoneId.systemDefault())
                sb.append(rfc7231.format(zdt))
            }
            if (ck.getPath() != null) {
                sb.append("; path=")
                sb.append(ck.getPath())
            }
            if (ck.getDomain() != null) {
                sb.append("; domain=")
                sb.append(ck.getDomain())
            }
            sb.append("; isSecure=")
            sb.append(ck.isSecure())
            sb.append("; isHttpOnly=")
            sb.append(ck.isHttpOnly())
        }
    }
}
