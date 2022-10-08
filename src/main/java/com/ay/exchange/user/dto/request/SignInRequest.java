package com.ay.exchange.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignInRequest {
    @Schema(description = "아이디",defaultValue = "testId")
    private String userId;

    @Schema(description = "비밀번호", defaultValue = "testPassword")
    private String password;
}
