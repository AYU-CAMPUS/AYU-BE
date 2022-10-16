package com.ay.exchange.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignInRequest {
    @Schema(description = "아이디")
    @Pattern(regexp = "^[a-zA-Z\\d]{6,15}$")
    private String userId;

    @Schema(description = "비밀번호")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z\\d!@#$%^&*]{9,15}$")
    private String password;
}
