package com.ay.exchange.exchange.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExchangeRequest {
    @Schema(description = "요청할 게시글 번호")
    private Long boardId;

    @Schema(description = "요청할 내 게시글 번호")
    private Long requesterBoardId;
}
