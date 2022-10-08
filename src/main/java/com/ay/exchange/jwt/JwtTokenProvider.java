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

@Component
@NoArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.access-expire-time}")
    private Long ACCESS_EXPIRE_TIME;
    @Value("${jwt.refresh-expire-time}")
    private Long REFRESH_EXPIRE_TIME;
    private Key key;


    @PostConstruct
    public void initKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String id, Authority authority) {
        Claims claims = Jwts.claims();
        claims.setSubject(id);
        claims.put("authority", authority);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(new Date().getTime() + ACCESS_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (JwtException | IllegalArgumentException e) { //유효하지 않은 토큰
            throw new JwtException("유효하지 않은 토큰");
        }
    }

    public Authority getAuthority(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("authority", Authority.class);
        } catch (JwtException | IllegalArgumentException e) { //유효하지 않은 토큰
            throw new JwtException("유효하지 않은 토큰");
        }
    }

    public String createVerificationCodeToken(
            String verificationCode
            , String email
    ) {
        Claims claims = Jwts.claims();
        claims.setSubject(email);
        claims.put("verificationCode", verificationCode);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(new Date().getTime() + 180000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getVerificationCode(String verificationCodeToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(verificationCodeToken)
                    .getBody()
                    .get("verificationCode", String.class);
        } catch (JwtException | IllegalArgumentException e) { //유효하지 않은 토큰
            throw new JwtException("유효하지 않은 토큰");
        }
    }

    public String getEmail(String token){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) { //유효하지 않은 토큰
            throw new JwtException("유효하지 않은 토큰");
        }
    }
}
