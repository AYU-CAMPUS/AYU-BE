package com.ay.exchange.oauth.controller;

import com.ay.exchange.common.error.dto.ErrorDto;
import com.ay.exchange.oauth.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
@Tag(name = "로그인", description = "oauth2 로그인 관련 api")
public class Oauth2Controller {

    @Operation(summary = "oauth2 로그인",
            description = "로그인에 대한 api명세서를 보여주기 위한 것으로 요청 시 동작하지 않습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "404", description = "안양대학교 웹메일만 로그인이 가능합니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    @GetMapping("/login")
    public LoginResponse loginGoogle() {
        return new LoginResponse(null, null);
    }

}