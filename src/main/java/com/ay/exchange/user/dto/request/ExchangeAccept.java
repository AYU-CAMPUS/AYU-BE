package com.ay.exchange.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExchangeAccept {
    @Schema(description = "교환 고유 번호")
    private Long exchangeId;

    @Schema(description = "신청자 고유 아이디")
    private String requesterId;

    @Schema(description = "내 게시글 번호")
    private Long boardId;

    @Schema(description = "신청자 게시글 번호")
    private Long requesterBoardId;
}