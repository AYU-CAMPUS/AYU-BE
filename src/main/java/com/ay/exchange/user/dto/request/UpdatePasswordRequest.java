package com.ay.exchange.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class UpdatePasswordRequest {
    @Schema(description = "유저 학교 웹메일")
    private String email;

    @Schema(description = "유저 비밀번호")
    private String password;
}
