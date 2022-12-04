package com.ay.exchange.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class VerificationCodeRequest {
    @Schema(description = "학교 웹메일")
    @Pattern(regexp = "^[a-zA-Z\\d-_.]{3,30}$")
    private String email;

    @Schema(description = "사용자가 입력한 인증번호")
    @NotBlank
    private String number;
}