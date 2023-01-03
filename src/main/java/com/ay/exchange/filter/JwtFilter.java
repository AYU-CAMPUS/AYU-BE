package com.ay.exchange.filter;

import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.user.entity.vo.Authority;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Integer COOKIE_EXPIRE_TIME;
    private final String DOMAIN;


    private static final Set<String> passUri = new HashSet<>(List.of("/user/existence-nickname", "/management/request-board", "/management/suspension", "/board"));
    private static final String regexUri = "/board/content/\\d+|/board/\\d+|/comment/\\d+";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = findToken(request.getCookies());
        if(token == null){
            throw new JwtException("존재하지 않는 토큰");
        }

        try {
            String email = jwtTokenProvider.getUserEmail(token);
            if (Boolean.FALSE.equals(redisTemplate.hasKey(email))) {
                throw new JwtException("유효하지 않은 토큰");
            }
        } catch (JwtException | IllegalArgumentException e) { //액세스 토큰 만료
            String refreshToken = (String) redisTemplate.opsForValue()
                    .get(token);

            if (refreshToken != null) { //리프레쉬 토큰이 존재하면 액세스 토큰 재발급
                System.out.println("재발급 받았다.");
                String email = jwtTokenProvider.getUserEmail(refreshToken);
                Authority authority = Authority.valueOf(jwtTokenProvider.getAuthority(refreshToken));
                String accessToken = jwtTokenProvider.createToken(email, authority);
                response.setHeader(HttpHeaders.SET_COOKIE, makeCookie(accessToken));
                redisTemplate.opsForValue()
                        .set(email, accessToken, COOKIE_EXPIRE_TIME, TimeUnit.SECONDS);
                redisTemplate.rename(token, accessToken);
                response.sendRedirect(request.getRequestURL().toString());
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        ZoneId seoulZoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime seoulCurrentTime = ZonedDateTime.now(seoulZoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd hh:mm");
        String formattedDate = seoulCurrentTime.format(formatter);

        System.out.println(formattedDate+" "+request.getRequestURI()+" => "+getClientIP(request));
        if (passUri.contains(request.getRequestURI())) {
            return true;
        }
        if (Pattern.matches(regexUri, request.getRequestURI())) {
            return true;
        }
        return false;
    }

    private String findToken(Cookie[] cookies) {
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(cookie -> "token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private String makeCookie(String token) {
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .domain(DOMAIN)
                .path("/")
                .maxAge(COOKIE_EXPIRE_TIME)
                .secure(true)
                .sameSite("None").build();
        return cookie.toString();
    }

    public static String getClientIP(HttpServletRequest request){
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }
}
