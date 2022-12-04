package com.ay.exchange.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindIdResponse {
    @Schema(description = "유저 아이디")
    private String userId;

    @Schema(description = "프로필 이미지 경로")
    private String profileImage;
}