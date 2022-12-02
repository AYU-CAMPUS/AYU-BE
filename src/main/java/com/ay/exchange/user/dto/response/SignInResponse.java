package com.ay.exchange.user.dto.response;

import com.ay.exchange.user.entity.vo.Authority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInResponse {
    @Schema(description = "액세스 토큰")
    private String token;

    @Schema(description = "닉네임")
    private String nickName;

    @Schema(description = "권한")
    private Authority authority;

    @Schema(description = "정지기간")
    private String suspendedDate;
}
