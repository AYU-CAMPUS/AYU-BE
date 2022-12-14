package com.ay.exchange.oauth.controller;

import com.ay.exchange.common.error.dto.ErrorDto;
import com.ay.exchange.common.error.exception.ErrorException;
import com.ay.exchange.jwt.JwtTokenProvider;
import com.ay.exchange.oauth.dto.LoginResponse;
import com.ay.exchange.oauth.service.Oauth2Service;
import com.ay.exchange.user.dto.query.UserInfoDto;
import com.ay.exchange.user.entity.vo.Authority;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
@Tag(name = "로그인", description = "oauth2 로그인 관련 api")
public class Oauth2Controller {
    private final Oauth2Service oauth2Service;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "oauth2 로그인",
            description = "로그인에 대한 api명세서를 보여주기 위한 것으로 요청 시 동작하지 않습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "404", description = "안양대학교 웹메일만 로그인이 가능합니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    @GetMapping("/login")
    public LoginResponse loginGoogle() {
        return new LoginResponse(null,null,null);
    }

    @Operation(summary = "임시 토큰 발급 요청", description = "임시 토큰 발급 요청")
    @GetMapping("/temp/login")
    public LoginResponse testLogin(){
        UserInfoDto userInfoDto = oauth2Service.findUserByEmail("test@gs.anyang.ac.kr");
        return new LoginResponse(jwtTokenProvider.createToken("test@gs.anyang.ac.kr", "test", Authority.Admin),"test",0);
    }
}
