package com.ay.exchange.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignUpRequest {
    @Schema(description = "유저 아이디")
    private String userId;

    @Schema(description = "유저 비밀번호")
    private String password;

    @Schema(description = "유저 닉네임")
    private String nickName;

    @Schema(description = "유저 학교 웹메일")
    private String email;
}
