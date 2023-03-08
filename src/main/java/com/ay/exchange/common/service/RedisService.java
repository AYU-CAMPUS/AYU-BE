package com.ay.exchange.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.ay.exchange.common.util.EncryptionUtil.getAccessExpireTime;
import static com.ay.exchange.common.util.EncryptionUtil.getRefreshExpireTime;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void deleteUserInfo(String token, String email) {
        redisTemplate.delete(token);
        redisTemplate.delete(email);
    }

    public void deleteBeforeToken(String email) {
        String beforeAccessToken = (String) redisTemplate.opsForValue()
                .get(email);

        if (beforeAccessToken != null) {
            redisTemplate.delete(beforeAccessToken); //기존 리프레쉬 토큰 삭제
        }
    }

    public void addAllTokens(String accessToken, String refreshToken, String email) {
        addAccessToken(accessToken, email);
        addRefreshToken(accessToken, refreshToken);
    }

    public void addAccessToken(String accessToken, String email) {
        redisTemplate.opsForValue()
                .set(email, accessToken, getAccessExpireTime(), TimeUnit.MILLISECONDS);
    }

    public void addRefreshToken(String accessToken, String refreshToken) {
        redisTemplate.opsForValue()
                .set(accessToken, refreshToken, getRefreshExpireTime(), TimeUnit.MILLISECONDS);
    }

    public void renameAccessToken(String token, String accessToken) {
        redisTemplate.rename(token, accessToken);
    }

    public boolean hasKey(String email) {
        return Objects.equals(redisTemplate.hasKey(email), false);
    }

    public String getRefreshToken(String token) {
        return (String) redisTemplate.opsForValue()
                .get(token);
    }
}
