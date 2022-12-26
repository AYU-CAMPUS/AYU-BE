package com.ay.exchange.management.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UserInfo {
    @Schema(description = "웹 메일")
    private String email;

    @Schema(description = "닉네임")
    private String nickName;

    @Schema(description = "생성일")
    private String createdDate;

    @Schema(description = "정지 기간 없으면 null")
    private String suspendedDate;

    @Schema(description = "정지 사유 없으면 null")
    private String reason;
}
