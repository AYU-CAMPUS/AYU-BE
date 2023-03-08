package com.ay.exchange.oauth.handler;

import com.ay.exchange.common.error.dto.ErrorDto;
import com.ay.exchange.common.service.RedisService;
import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.oauth.facade.Oauth2Facade;
import com.ay.exchange.user.dto.query.UserInfoDto;
import com.ay.exchange.user.entity.vo.Authority;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ay.exchange.common.util.CookieUtil.makeCookie;
import static com.ay.exchange.common.util.EncryptionUtil.*;
import static com.ay.exchange.common.util.NickNameGenerator.createRandomNickName;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final Oauth2Facade oauth2Facade;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RedisService redisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        settingResponse(response);

        String email = (String) oAuth2User.getAttribute("email");
//        if (!oauth2Service.isAnyangDomain((String) oAuth2User.getAttribute("hd"))) {
//            makeError(response);
//            return;
//        }
        redisService.deleteBeforeToken(email);  //이중로그인 방지와 반복된 로그인 요청에 쌓이는 데이터를 막기 위해 이전 토큰들은 삭제한다.

        UserInfoDto userInfoDto = oauth2Facade.findUserByEmail(email);

        if (checkExistingUser(userInfoDto)) { //기존 회원
            String accessToken = jwtTokenProvider.createToken(email, userInfoDto.getAuthority());
            String refreshToken = jwtTokenProvider.createRefreshToken(email, userInfoDto.getAuthority());
            redisService.addAllTokens(accessToken, refreshToken, email);
            makeResponse(response, accessToken, email);
            return;
        }

        //최초 로그인
        try {
            String nickName = createRandomNickName();
            oauth2Facade.saveUser(email, nickName);
            String accessToken = jwtTokenProvider.createToken(email, Authority.User);
            String refreshToken = jwtTokenProvider.createRefreshToken(email, Authority.User);
            redisService.addAllTokens(accessToken, refreshToken, email);
            makeResponse(response, accessToken, email);
        } catch (Exception e) { //만약에 랜덤닉네임을 받았지만 간발의 차이로 겹칠 경우도 있지만 일단 유저가 아니라고 예외코드를 보낸다.
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

    private void makeResponse(HttpServletResponse response, String token, String email) throws IOException {
        response.setHeader(HttpHeaders.SET_COOKIE, makeCookie(token));
        if (isDeveloper(email)) {
            response.sendRedirect(UriComponentsBuilder.fromUriString(getDevUrl())
                    .build()
                    .toUriString());
            return;
        }
        response.sendRedirect(UriComponentsBuilder.fromUriString(getClientUrl())
                .build()
                .toUriString());
    }

    private void makeError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.NOT_FOUND.value());

        String result = objectMapper.writeValueAsString(new ErrorDto(HttpStatus.NOT_FOUND.name(), "안양대학교 웹메일만 로그인이 가능합니다."));
        response.getWriter().write(result);
    }

}
