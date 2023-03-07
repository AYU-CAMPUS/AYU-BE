package com.ay.exchange.common.util;

import org.springframework.http.ResponseCookie;

import static com.ay.exchange.common.util.EncryptionUtil.getCookieExpireTime;
import static com.ay.exchange.common.util.EncryptionUtil.getDOMAIN;

public class CookieUtil {
    public static String makeCookie(String token) {
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .domain(getDOMAIN())
                .path("/")
                .maxAge(getCookieExpireTime())
                .secure(true)
                .sameSite("None").build();
        return cookie.toString();
    }

    public static String removeCookie() {
        ResponseCookie cookie = ResponseCookie.from("token", null)
                .httpOnly(true)
                .domain(getDOMAIN())
                .path("/")
                .maxAge(0)
                .secure(true)
                .sameSite("None").build();
        return cookie.toString();
    }
}
