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
    private static String secretKey;

    @Value("${jwt.secret-key}")
    private static void setSecretKey(String secretKey) {
        EncryptionUtil.secretKey = secretKey;
    }

    @Value("${developer.email}")
    private void setDeveloperEmail(Set<String> developerEmail) {
        this.developerEmail = developerEmail;
    }

    @Value("${jwt.access-expire-time}")
    private static void setAccessExpireTime(Long accessExpireTime) {
        ACCESS_EXPIRE_TIME = accessExpireTime;
    }

    @Value("${jwt.refresh-expire-time}")
    private static void setRefreshExpireTime(Long refreshExpireTime) {
        REFRESH_EXPIRE_TIME = refreshExpireTime;
    }

    @Value("${cookie.expire-time}")
    private void setCookieExpireTime(Integer cookieExpireTime) {
        COOKIE_EXPIRE_TIME = cookieExpireTime;
    }

    @Value("${cookie.domain}")
    private static void setDOMAIN(String DOMAIN) {
        EncryptionUtil.DOMAIN = DOMAIN;
    }

    @Value("${address.client}")
    private static void setClientUrl(String clientUrl) {
        CLIENT_URL = clientUrl;
    }

    @Value("${address.dev}")
    private static void setDevUrl(String devUrl) {
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

    public static String getSecretKey() {
        return secretKey;
    }

    public static boolean isDeveloper(String email){
        return developerEmail.contains(email);
    }
}
