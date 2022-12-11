package com.ay.exchange.management.handler;

import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.user.entity.vo.Authority;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;


@Component(value = "Permission")
@RequiredArgsConstructor
public class Permission {
    private final JwtTokenProvider jwtTokenProvider;

    public boolean isManager(String token) {
        String authority = jwtTokenProvider.getAuthority(token);
        return authority.equals(Authority.Admin.name());
    }
}