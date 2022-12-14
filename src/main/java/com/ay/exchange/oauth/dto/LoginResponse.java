package com.ay.exchange.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    @Schema(description = "닉네임")
    private String nickName;

    @Schema(description = "교환요청수")
    private Integer numberOfRequestExchange;
}