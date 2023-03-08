package com.ay.exchange.jwt;

import com.ay.exchange.user.entity.vo.Authority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

import static com.ay.exchange.common.util.EncryptionUtil.*;

@Component
@NoArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret-key}")
    private String secretKey;
    private Key secretMasterKey;

    @PostConstruct
    public void initKeys() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        secretMasterKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String email, Authority authority) {
        Claims claims = Jwts.claims();
        claims.setSubject(email);
        claims.put("authority", authority.name());

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(new Date().getTime() + getAccessExpireTime()))
                .signWith(secretMasterKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateToken(String accessToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretMasterKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("유효하지 않은 토큰");
        }
    }

    public String getAuthority(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretMasterKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("authority", String.class);
        } catch (JwtException | IllegalArgumentException e) { //유효하지 않은 토큰
            e.printStackTrace();
            throw new JwtException("유효하지 않은 토큰");
        }
    }

    public String getUserEmail(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretMasterKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) { //유효하지 않은 토큰
            throw new JwtException("유효하지 않은 토큰");
        }
    }

    public String createRefreshToken(String email, Authority authority) {
        Claims claims = Jwts.claims();
        claims.setSubject(email);
        claims.put("authority", authority);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(new Date().getTime() + getRefreshExpireTime()))
                .signWith(secretMasterKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
