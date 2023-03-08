package com.ay.exchange.common.util;

public class NickNameGenerator {
    public static String createRandomNickName() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append((char) (Math.random() * 26 + 65));
        }
        return sb.toString();
    }
}
