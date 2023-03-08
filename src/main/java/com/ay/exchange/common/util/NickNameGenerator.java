package com.ay.exchange.common.util;

public class NickNameGenerator {
    private String makeRandomNickName() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append((char) (Math.random() * 26 + 65));
        }
        return sb.toString();
    }

//    private String settingUserNickName() {
//        String randomNickName = null;
//        while (true) {
//            randomNickName = makeRandomNickName();
//            if (!oauth2Service.checkExistsUserByNickName(randomNickName)) {
//                break;
//            }
//        }
//        return randomNickName;
//    }
}
