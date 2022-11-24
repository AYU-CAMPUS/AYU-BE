package com.ay.exchange.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExchangeInfo {
    @Schema(description = "교환 식별 번호")
    private Long exchangeId;

    @Schema(description = "신청일")
    private String applicationDate;

    @Schema(description = "신청자")
    private String applicant;

    @Schema(description = "자료명")
    private String title;

    @Schema(description = "게시글 번호")
    private Long boardId;
}
