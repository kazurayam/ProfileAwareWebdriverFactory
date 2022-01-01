package com.kazurayam.webdriverfactory.utils

import org.junit.Test
import org.openqa.selenium.Cookie

class CookieClassHelperTest {


    /**
     * public Cookie(java.lang.String name,
     java.lang.String value,
     java.lang.String domain,
     java.lang.String path,
     java.util.Date expiry,
     boolean isSecure,
     boolean isHttpOnly,
     java.lang.String sameSite)
     */
    @Test
    void test_overrideToString() {
        CookieClassHelper.overrideToString()
        Cookie ck = new Cookie("name", "value",
                "127.0.0.1", "/", new Date(), true, true)
        String s = ck.toString()
        println s
    }
}
