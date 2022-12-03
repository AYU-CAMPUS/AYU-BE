package com.ay.exchange.user.dto.response;

import com.ay.exchange.user.entity.vo.Authority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SignUpResponse {
    @Schema(description = "액세스 토큰")
    private String token;

    @Schema(description = "유저 닉네임")
    private String nickName;
}
