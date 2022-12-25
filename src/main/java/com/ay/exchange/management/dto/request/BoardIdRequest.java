package com.ay.exchange.management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BoardIdRequest {
    @Schema(description = "허가 또는 거절할 게시물 번호")
    private Long boardId;
}
