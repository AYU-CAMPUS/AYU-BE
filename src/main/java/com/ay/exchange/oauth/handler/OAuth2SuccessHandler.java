package com.ay.exchange.oauth.handler;

import com.ay.exchange.common.error.dto.ErrorDto;
import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.oauth.dto.LoginResponse;
import com.ay.exchange.oauth.service.Oauth2Service;
import com.ay.exchange.user.dto.query.UserInfoDto;
import com.ay.exchange.user.entity.vo.Authority;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final Oauth2Service oauth2Service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        settingResponse(response);

        String email = (String) oAuth2User.getAttribute("email");
//        if (!oauth2Service.isAnyangDomain((String) oAuth2User.getAttribute("hd"))) {
//            makeError(response);
//            return;
//        }

        UserInfoDto userInfoDto = oauth2Service.findUserByEmail(email);

        if (checkExistingUser(userInfoDto)) { //기존 회원
            String nickName = userInfoDto.getNickName();
            String token = jwtTokenProvider.createToken(email, userInfoDto.getNickName(), userInfoDto.getAuthority());
            makeResponse(response, token, nickName);
            return;
        }

        //최초 로그인
        String nickName = settingUserNickName();
        try {
            oauth2Service.saveUser(email, nickName);
            String token = jwtTokenProvider.createToken(email, nickName, Authority.User);
            makeResponse(response, token, nickName);
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

    private void makeResponse(HttpServletResponse response, String token, String nickName) throws IOException {
        String result = objectMapper.writeValueAsString(new LoginResponse(token, nickName, 0));
        response.getWriter().write(result);
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
