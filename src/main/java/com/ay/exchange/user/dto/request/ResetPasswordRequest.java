package com.ay.exchange.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResetPasswordRequest {
    @Schema(description = "유저 학교 웹메일")
    @Pattern(regexp = "^[a-zA-Z\\d-_.]{3,30}$")
    private String email;

    @Schema(description = "변경할 비밀번호")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z\\d!@#$%^&*]{9,15}$")
    private String password;
}
