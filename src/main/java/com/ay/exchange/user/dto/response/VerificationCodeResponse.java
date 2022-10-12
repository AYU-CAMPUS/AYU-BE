package com.ay.exchange.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VerificationCodeResponse {
    @Schema(description = "인증번호가 저장되어 있는 토큰")
    private String verificationCode;

    @Schema(description = "테스트를 위한 인증번호 값")
    private String code;
}
