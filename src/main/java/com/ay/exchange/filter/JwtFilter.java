package com.ay.exchange.filter;

import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.user.entity.vo.Authority;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

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
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Integer COOKIE_EXPIRE_TIME;
    private final String DOMAIN;
    private final String URL;

    private static final Set<String> passUri = new HashSet<>(List.of("/login/oauth2/code/google", "/oauth2/authorization/google"));
    private static final String regexUri = "/board/content/\\d+|/board/\\d+";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            log.info("OPTIONS");
//            setCorsHeader(response);
//            response.setStatus(HttpServletResponse.SC_OK);
//            return;
        }

        if(isAuthentication(request, response)){
            filterChain.doFilter(request, response);
        }

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        ZoneId seoulZoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime seoulCurrentTime = ZonedDateTime.now(seoulZoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd hh:mm");
        String formattedDate = seoulCurrentTime.format(formatter);

        log.info(formattedDate + " " + request.getRequestURI() + " => " + getClientIP(request) + " " + request.getMethod());
        if (passUri.contains(request.getRequestURI())) {
            return true;
        }
        if (Pattern.matches(regexUri, request.getRequestURI()) && request.getMethod().equals("GET")) {
            return true;
        }
        if(request.getMethod().equals("OPTIONS")){
            log.info("OPTIONS2");
            return true;
        }
        return false;
    }

    private boolean isAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = findToken(request.getCookies());
        log.info("USER TOKEN {}",token);

        if (token == null) {
            setCorsHeader(response);
            throw new JwtException("유효하지 않은 토큰");
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
                log.info("재발급 완료");
                String email = jwtTokenProvider.getUserEmail(refreshToken);
                Authority authority = Authority.valueOf(jwtTokenProvider.getAuthority(refreshToken));
                String accessToken = jwtTokenProvider.createToken(email, authority);
                response.setHeader(HttpHeaders.SET_COOKIE, makeCookie(accessToken));
                redisTemplate.opsForValue()
                        .set(email, accessToken, COOKIE_EXPIRE_TIME, TimeUnit.SECONDS);
                redisTemplate.rename(token, accessToken);
                setCorsHeader(response);
                response.sendRedirect(request.getRequestURL().toString());
                return false;
            }else{
                setCorsHeader(response);
                throw new JwtException("유효하지 않은 토큰");
            }
        }
        setCorsHeader(response);
        return true;
    }

    private void redirectLogin(HttpServletResponse response) throws IOException {
        setCorsHeader(response);

        response.sendRedirect(UriComponentsBuilder.fromUriString(URL)
                .build()
                .toUriString());
    }

    private void setCorsHeader(HttpServletResponse response){
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods","*");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "Origin, X-Requested-With, Content-Type, Accept, Authorization");
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

    public static String getClientIP(HttpServletRequest request) {
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
