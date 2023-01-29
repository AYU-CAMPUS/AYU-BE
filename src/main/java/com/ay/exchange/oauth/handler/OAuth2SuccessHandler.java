package com.ay.exchange.oauth.handler;

import com.ay.exchange.common.error.dto.ErrorDto;
import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.oauth.service.Oauth2Service;
import com.ay.exchange.user.dto.query.UserInfoDto;
import com.ay.exchange.user.entity.vo.Authority;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final Oauth2Service oauth2Service;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${cookie.expire-time}")
    private Integer COOKIE_EXPIRE_TIME;
    @Value("${cookie.domain}")
    private String DOMAIN;

    @Value("${jwt.access-expire-time}")
    private Long ACCESS_EXPIRE_TIME;

    @Value("${jwt.refresh-expire-time}")
    private Long REFRESH_EXPIRE_TIME;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        settingResponse(response);

        String email = (String) oAuth2User.getAttribute("email");
//        if (!oauth2Service.isAnyangDomain((String) oAuth2User.getAttribute("hd"))) {
//            makeError(response);
//            return;
//        }
        deleteBeforeTokenInRedis(email);  //이중로그인 방지와 반복된 로그인 요청에 쌓이는 데이터를 막기 위해 이전 토큰들은 삭제한다.

        UserInfoDto userInfoDto = oauth2Service.findUserByEmail(email);

        if (checkExistingUser(userInfoDto)) { //기존 회원
            String accessToken = jwtTokenProvider.createToken(email, userInfoDto.getAuthority());
            String refreshToken = jwtTokenProvider.createRefreshToken(email, userInfoDto.getAuthority());
            addTokenInRedis(accessToken, refreshToken, email);
            makeResponse(response, accessToken);
            return;
        }

        //최초 로그인
        String nickName = settingUserNickName();
        try {
            oauth2Service.saveUser(email, nickName);
            String accessToken = jwtTokenProvider.createToken(email, Authority.User);
            String refreshToken = jwtTokenProvider.createRefreshToken(email, Authority.User);
            addTokenInRedis(accessToken, refreshToken, email);
            makeResponse(response, accessToken);
        } catch (Exception e) { //만약에 랜덤닉네임을 받았지만 간발의 차이로 겹칠 경우도 있지만 일단 유저가 아니라고 예외코드를 보낸다.
            makeError(response);
        }
    }

    private void deleteBeforeTokenInRedis(String email) {
        String beforeAccessToken = (String) redisTemplate.opsForValue()
                .get(email);

        if (beforeAccessToken != null) {
            redisTemplate.delete(beforeAccessToken); //기존 리프레쉬 토큰 삭제
        }
    }

    private boolean checkExistingUser(UserInfoDto userInfoDto) {
        return userInfoDto != null;
    }

    private void settingResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
    }

    private void makeResponse(HttpServletResponse response, String token) throws IOException {
        response.setHeader(HttpHeaders.SET_COOKIE, makeCookie(token));
        response.sendRedirect(UriComponentsBuilder.fromUriString("http://localhost:3000")
                .build()
                .toUriString());
    }

    private void addTokenInRedis(String accessToken, String refreshToken, String email) {
        //액세스 토큰 저장
        redisTemplate.opsForValue()
                .set(email, accessToken, ACCESS_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        //리프레쉬 저장
        redisTemplate.opsForValue()
                .set(accessToken, refreshToken, REFRESH_EXPIRE_TIME, TimeUnit.MILLISECONDS);

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

    private void makeError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.NOT_FOUND.value());

        String result = objectMapper.writeValueAsString(new ErrorDto(HttpStatus.NOT_FOUND.name(), "안양대학교 웹메일만 로그인이 가능합니다."));
        response.getWriter().write(result);
    }

    private String makeRandomNickName() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append((char) (Math.random() * 26 + 65));
        }
        return sb.toString();
    }

    private String settingUserNickName() {
        String randomNickName = null;
        while (true) {
            randomNickName = makeRandomNickName();
            if (!oauth2Service.checkExistsUserByNickName(randomNickName)) {
                break;
            }
        }
        return randomNickName;
    }
}
