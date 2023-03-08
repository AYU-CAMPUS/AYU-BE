package com.ay.exchange.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void deleteUserInfo(String token, String email) {
        redisTemplate.delete(token);
        redisTemplate.delete(email);
    }
}
