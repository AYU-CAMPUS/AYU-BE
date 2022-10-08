package com.ay.exchange.board.dto.response;

import com.ay.exchange.board.dto.query.BoardInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@AllArgsConstructor
@Getter
public class BoardResponse {
    @Schema(description = "전체 페이지 수")
    private int totalPages;

    @Schema(description = "게시글 목록")
    private List<BoardInfoDto> boardList;
}
