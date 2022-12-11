package com.ay.exchange.management.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Getter
public class RequestBoardResponse {
    @Schema(description = "전체 요청 페이지 수")
    private Long totalPages;

    @Schema(description = "게시글 정보")
    private List<BoardInfo> boardInfos;
}