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
    private static final String regexUri = "/board/content/\\d+";
    private static final Set<String> passUri = new HashSet<>(List.of(
            "/login/oauth2/code/google",
            "/oauth2/authorization/google"));


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.nanoTime();

        if (isAuthentication(request, response)) {
            filterChain.doFilter(request, response);
        }

        double elapsedTimeInMilliseconds = (double) (System.nanoTime() - startTime) / 1_000_000;
        log.info("총 실행 시간: {} ms", elapsedTimeInMilliseconds);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (passUri.contains(request.getRequestURI())) { //oauth2로그인은 Security Filter Chain에서 로직을 수행하기 때문에 ignore해주면 안된다.
            return true;
        }
        ZoneId seoulZoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime seoulCurrentTime = ZonedDateTime.now(seoulZoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd hh:mm");
        String formattedDate = seoulCurrentTime.format(formatter);
        log.info("{} {} {} => {} {} url: {}", request.getHeader(HttpHeaders.ORIGIN), formattedDate, request.getRequestURI(), getClientIP(request), request.getMethod(), request.getHeader("url"));

        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        return false;
    }

    private boolean isAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = findToken(request.getCookies());
        String origin = request.getHeader(HttpHeaders.ORIGIN);

        boolean isShowBoardContent = Pattern.matches(regexUri, request.getRequestURI()) && request.getMethod().equals("GET");

        if (token == null) {
            setCorsHeader(response, origin);
            if(isShowBoardContent){
                return true;
            }
            throw new JwtException("유효하지 않은 토큰");
        }

        try {
            String email = jwtTokenProvider.getUserEmail(token);
            if (!redisService.hasKey(email)) {
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
