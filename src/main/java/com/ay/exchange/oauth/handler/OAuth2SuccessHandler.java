package com.ay.exchange.oauth.handler;

import com.ay.exchange.common.error.dto.ErrorDto;
import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.oauth.dto.LoginResponse;
import com.ay.exchange.oauth.service.Oauth2Service;
import com.ay.exchange.user.dto.query.UserInfoDto;
import com.ay.exchange.user.entity.vo.Authority;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final Oauth2Service oauth2Service;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${cookie.expire-time}")
    private Integer COOKIE_EXPIRE_TIME;
    @Value("${cookie.domain}")
    private String DOMAIN;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        //추후 로그인에 성공하면 redis를 활용하여 유저 이메일에 대해서 jwt 삭제해줘야 함.

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        settingResponse(response);

        String email = (String) oAuth2User.getAttribute("email");
//        if (!oauth2Service.isAnyangDomain((String) oAuth2User.getAttribute("hd"))) {
//            makeError(response);
//            return;
//        }

        UserInfoDto userInfoDto = oauth2Service.findUserByEmail(email);

        if (checkExistingUser(userInfoDto)) { //기존 회원
            makeResponse(response, email, userInfoDto.getNickName(), userInfoDto.getAuthority());
            return;
        }

        //최초 로그인
        String nickName = settingUserNickName();
        try {
            oauth2Service.saveUser(email, nickName);
            makeResponse(response, email, nickName, Authority.User);
        } catch (Exception e) { //만약에 랜덤닉네임을 받았지만 간발의 차이로 겹칠 경우 일단 유저가 아니라고 예외코드를 보낸다.
            makeError(response);
        }
    }

    private boolean checkExistingUser(UserInfoDto userInfoDto) {
        return userInfoDto != null;
    }

    private void settingResponse(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
    }

    private void makeResponse(HttpServletResponse response, String email, String nickName, Authority authority) throws IOException {
        response.addCookie(makeCookie(jwtTokenProvider.createToken(email, nickName, authority)));
        response.sendRedirect(UriComponentsBuilder.fromUriString("http://localhost:3000/login")
                .queryParam("numberOfRequestExchange", 0)
                .queryParam("nickName", nickName)
                .build()
                .toUriString());

//        String result = objectMapper.writeValueAsString(new LoginResponse(nickName, 0));
//        response.getWriter().write(result);
    }

    private Cookie makeCookie(String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(COOKIE_EXPIRE_TIME);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setDomain(DOMAIN);
        return cookie;
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
            if (!oauth2Service.checkExistsUserByByEmail(randomNickName)) {
                break;
            }
        }
        return randomNickName;
    }
}
