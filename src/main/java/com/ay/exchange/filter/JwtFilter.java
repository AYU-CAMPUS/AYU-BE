package com.ay.exchange.filter;

import com.ay.exchange.common.service.RedisService;
import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.user.entity.vo.Authority;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import org.springframework.stereotype.Component;
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

import java.util.regex.Pattern;

import static com.ay.exchange.common.util.CookieUtil.makeCookie;
import static com.ay.exchange.common.util.EncryptionUtil.*;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private static final Set<String> passUri = new HashSet<>(List.of(
            "/login/oauth2/code/google",
            "/oauth2/authorization/google",
            "/swagger",
            "/swagger-ui/index.html",
            "/swagger-ui/swagger-ui.css",
            "/swagger-ui/index.css",
            "/swagger-ui/swagger-ui-bundle.js",
            "/swagger-ui/swagger-ui-standalone-preset.js",
            "/swagger-ui/swagger-initializer.js",
            "/v3/api-docs/swagger-config",
            "/swagger-ui/favicon-32x32.png",
            "/v3/api-docs/user-api",
            "/v3/api-docs/board-api",
            "/v3/api-docs/comment-api",
            "/v3/api-docs/report-api",
            "/v3/api-docs/oauth2-api",
            "/v3/api-docs/exchange-api",
            "/v3/api-docs/management-api",
            "/favicon.ico"));
    private static final String regexUri = "/board/content/\\d+|/board/\\d+";


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isAuthentication(request, response)) {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        ZoneId seoulZoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime seoulCurrentTime = ZonedDateTime.now(seoulZoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd hh:mm");
        String formattedDate = seoulCurrentTime.format(formatter);

        if (passUri.contains(request.getRequestURI())) {
            return true;
        }
        log.info("{} {} {} => {} {}", request.getHeader(HttpHeaders.ORIGIN), formattedDate, request.getRequestURI(), getClientIP(request), request.getMethod());
        log.info("url: {}, ", request.getHeader("url"));

        if (Pattern.matches(regexUri, request.getRequestURI()) && request.getMethod().equals("GET")) {
            return true;
        }
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        return false;
    }

    private boolean isAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = findToken(request.getCookies());
        log.info("USER TOKEN {}", token);
        String origin = request.getHeader(HttpHeaders.ORIGIN);

        if (token == null) {
            setCorsHeader(response, origin);
            throw new JwtException("유효하지 않은 토큰");
        }

        try {
            String email = jwtTokenProvider.getUserEmail(token);
            if (redisService.hasKey(email)) {
                throw new JwtException("유효하지 않은 토큰");
            }
        } catch (JwtException | IllegalArgumentException e) { //액세스 토큰 만료
            String refreshToken = redisService.getRefreshToken(token);
            setCorsHeader(response, origin);
            if (refreshToken != null) { //리프레쉬 토큰이 존재하면 액세스 토큰 재발급
                log.info("재발급 완료");
                String email = jwtTokenProvider.getUserEmail(refreshToken);
                Authority authority = Authority.valueOf(jwtTokenProvider.getAuthority(refreshToken));
                String accessToken = jwtTokenProvider.createToken(email, authority);
                response.setHeader(HttpHeaders.SET_COOKIE, makeCookie(accessToken));
                redisService.addAccessToken(accessToken, email);
                redisService.renameAccessToken(token, accessToken);
                response.sendRedirect(request.getRequestURL().toString());
                return false;
            } else {
                throw new JwtException("유효하지 않은 토큰");
            }
        }
        setCorsHeader(response, origin);
        return true;
    }

    private void setCorsHeader(HttpServletResponse response, String url) {
        if (url == null) return;
        if (url.equals(getClientUrl()) || url.equals(getDevUrl())) {
            response.setHeader("Access-Control-Allow-Origin", url);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers",
                    "Origin, X-Requested-With, Content-Type, Accept, Authorization");
        }
    }

    private String findToken(Cookie[] cookies) {
        if (cookies == null) return null;
        return Arrays.stream(cookies)
                .filter(cookie -> "token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private static String getClientIP(HttpServletRequest request) {
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
