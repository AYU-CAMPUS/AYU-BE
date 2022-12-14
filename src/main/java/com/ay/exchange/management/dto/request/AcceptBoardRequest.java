package com.ay.exchange.management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AcceptBoardRequest {
    @Schema(description = "허가할 게시물 번호")
    private Long boardId;
}
