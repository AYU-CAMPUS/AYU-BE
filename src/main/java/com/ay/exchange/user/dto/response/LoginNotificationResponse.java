package com.ay.exchange.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class LoginNotificationResponse {
    @Schema(description = "유저 닉네임")
    private String nickName;

    @Schema(description = "교환 요청 수")
    private Long numberOfExchange;

    @Schema(description = "정지 기간 정지가 아니면 NULL")
    private String suspendedDate;

    @Schema(description = "정지 회원이면 정지 사유")
    private String suspendedReason;

//    @Schema(description = "알림 수")
//    private Long numberOfNotification;
}