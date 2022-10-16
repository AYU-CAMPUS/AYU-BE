package com.ay.exchange.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignUpRequest {
    @Schema(description = "유저 아이디")
    @Pattern(regexp = "^[a-zA-Z\\d]{6,15}$")
    private String userId;

    @Schema(description = "유저 비밀번호")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z\\d!@#$%^&*]{9,15}$")
    private String password;

    @Schema(description = "유저 닉네임")
    @Pattern(regexp = "^[a-zA-Z\\d가-힣]{1,8}$")
    private String nickName;

    @Schema(description = "유저 학교 웹메일")
    @Pattern(regexp = "^[a-zA-Z\\d-_.]{3,30}$")
    private String email;
}