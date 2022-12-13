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
    @Value("${jwt.sign-up-key}")
    private String signUpKey;

    @Value("${jwt.find-user-key}")
    private String findUserKey;

    @Value("${jwt.access-expire-time}")
    private Long ACCESS_EXPIRE_TIME;
    @Value("${jwt.refresh-expire-time}")
    private Long REFRESH_EXPIRE_TIME;
    private Key secretMasterKey, signUpMasterKey, findUserMasterKey;
    private final int SIGN_UP=0;
    private final int FIND_USER=1;

    @PostConstruct
    public void initKeys() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        secretMasterKey = Keys.hmacShaKeyFor(keyBytes);

        byte[] keyBytes2 = Decoders.BASE64.decode(signUpKey);
        signUpMasterKey =Keys.hmacShaKeyFor(keyBytes2);

        byte[] keyBytes3 = Decoders.BASE64.decode(findUserKey);
        findUserMasterKey =Keys.hmacShaKeyFor(keyBytes3);
    }

    public String createToken(String userId, String nickName, Authority authority) {
        Claims claims = Jwts.claims();
        claims.setSubject(userId);
        claims.put("authority", authority.name());
        claims.put("nickName",nickName);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(new Date().getTime() + ACCESS_EXPIRE_TIME))
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
        } catch (JwtException | IllegalArgumentException e) { //유효하지 않은 토큰
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

    public String createVerificationCodeToken(int selection, String verificationCode, String email) {
        Claims claims = Jwts.claims();
        claims.setSubject(email);
        claims.put("verificationCode", verificationCode);

        switch(selection){
            case SIGN_UP:
                return Jwts.builder()
                        .setClaims(claims)
                        .setExpiration(new Date(new Date().getTime() + 180000))
                        .signWith(signUpMasterKey, SignatureAlgorithm.HS256)
                        .compact();
            default: //비밀번호 찾기
                return Jwts.builder()
                        .setClaims(claims)
                        .setExpiration(new Date(new Date().getTime() + 180000))
                        .signWith(findUserMasterKey, SignatureAlgorithm.HS256)
                        .compact();
        }

    }

    public String getVerificationCode(int selection, String verificationCodeToken) {
        try {
            switch(selection){
                case SIGN_UP:
                    return Jwts.parserBuilder()
                            .setSigningKey(signUpMasterKey)
                            .build()
                            .parseClaimsJws(verificationCodeToken)
                            .getBody()
                            .get("verificationCode", String.class);
                default: //비밀번호 찾기
                    return Jwts.parserBuilder()
                            .setSigningKey(findUserMasterKey)
                            .build()
                            .parseClaimsJws(verificationCodeToken)
                            .getBody()
                            .get("verificationCode", String.class);
            }
        } catch (JwtException | IllegalArgumentException e) { //유효하지 않은 토큰
            throw new JwtException("유효하지 않은 토큰");
        }
    }

    public String getUserEmail(String token){
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

    public String getNickName(String accessToken){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretMasterKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody()
                    .get("nickName",String.class);
        } catch (JwtException | IllegalArgumentException e) { //유효하지 않은 토큰
            throw new JwtException("유효하지 않은 토큰");
        }
    }

    public String getEmailBySignUpVerificationCode(String verificationCodeToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(signUpMasterKey)
                    .build()
                    .parseClaimsJws(verificationCodeToken)
                    .getBody()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) { //유효하지 않은 토큰
            throw new JwtException("유효하지 않은 토큰");
        }
    }

    public String getEmailByFindUserVerificationCode(String verificationCodeToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(findUserMasterKey)
                    .build()
                    .parseClaimsJws(verificationCodeToken)
                    .getBody()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) { //유효하지 않은 토큰
            throw new JwtException("유효하지 않은 토큰");
        }
    }

}
