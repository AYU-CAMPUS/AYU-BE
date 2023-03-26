package com.ay.exchange.exchange.dto.response;

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

    @Schema(description = "신청자 닉네임")
    private String requesterNickName;

    @Schema(description = "신청자 아이디")
    private String requesterId;

    @Schema(description = "자료명")
    private String title;

    @Schema(description = "내 게시글 번호")
    private Long boardId;

    @Schema(description = "신청자 게시글 번호")
    private Long requesterBoardId;

}