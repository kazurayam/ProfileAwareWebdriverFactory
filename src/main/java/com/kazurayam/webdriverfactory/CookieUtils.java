package com.kazurayam.webdriverfactory;

import org.openqa.selenium.Cookie;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class CookieUtils {
    /**
     * @returns "timestamp=Sat, 08 Jan 2022 05:13:04 GMT; expires=Sat, 08 Jan 2022 05:13:34 GMT; path=/; domain=127.0.0.1"
     */
    public static String stringifyCookie(Cookie cookie) {
        StringBuilder sb = new StringBuilder();
        sb.append(cookie.getName());
        sb.append("=");
        sb.append(cookie.getValue());
        sb.append("; ");
        sb.append("expires=");
        sb.append(formatDateInRFC7231(cookie.getExpiry()));
        sb.append("; path=");
        sb.append(cookie.getPath());
        sb.append("; domain=");
        sb.append(cookie.getDomain());
        return sb.toString();
    }

    private static String formatDateInRFC7231(Date date) {
        ZoneId zid = ZoneId.systemDefault();
        ZonedDateTime zdt = ZonedDateTime.ofInstant(date.toInstant(), zid);
        String formatted = rfc7231.format(zdt);
        return formatted;
    }

    private static final DateTimeFormatter rfc7231 = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).withZone(ZoneId.of("GMT"));
}
