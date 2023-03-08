package com.ay.exchange.common.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EncryptionUtil {
    private static Integer COOKIE_EXPIRE_TIME;
    private static String DOMAIN;
    private static String CLIENT_URL;
    private static String DEV_URL;
    private static Long ACCESS_EXPIRE_TIME;
    private static Long REFRESH_EXPIRE_TIME;
    private static Set<String> developerEmail;

    @Value("${developer.email}")
    private void setDeveloperEmail(Set<String> developerEmail) {
        EncryptionUtil.developerEmail = developerEmail;
    }

    @Value("${jwt.access-expire-time}")
    private void setAccessExpireTime(Long accessExpireTime) {
        ACCESS_EXPIRE_TIME = accessExpireTime;
    }

    @Value("${jwt.refresh-expire-time}")
    private void setRefreshExpireTime(Long refreshExpireTime) {
        REFRESH_EXPIRE_TIME = refreshExpireTime;
    }

    @Value("${cookie.expire-time}")
    private void setCookieExpireTime(Integer cookieExpireTime) {
        COOKIE_EXPIRE_TIME = cookieExpireTime;
    }

    @Value("${cookie.domain}")
    private void setDOMAIN(String DOMAIN) {
        EncryptionUtil.DOMAIN = DOMAIN;
    }

    @Value("${address.client}")
    private void setClientUrl(String clientUrl) {
        CLIENT_URL = clientUrl;
    }

    @Value("${address.dev}")
    private void setDevUrl(String devUrl) {
        DEV_URL = devUrl;
    }

    public static Integer getCookieExpireTime() {
        return COOKIE_EXPIRE_TIME;
    }

    public static String getDOMAIN() {
        return DOMAIN;
    }

    public static String getClientUrl() {
        return CLIENT_URL;
    }

    public static String getDevUrl() {
        return DEV_URL;
    }

    public static Long getAccessExpireTime() {
        return ACCESS_EXPIRE_TIME;
    }

    public static Long getRefreshExpireTime() {
        return REFRESH_EXPIRE_TIME;
    }

    public static boolean isDeveloper(String email){
        return developerEmail.contains(email);
    }
}
