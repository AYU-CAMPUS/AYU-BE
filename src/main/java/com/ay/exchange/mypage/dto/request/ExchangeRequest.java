package com.ay.exchange.mypage.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExchangeRequest {
    @Schema(description = "신청자 고유 아이디")
    private String applicantId;

    @Schema(description = "게시글 번호")
    private Long boardId;
}