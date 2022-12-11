package com.ay.exchange.management.dto.response;

import com.ay.exchange.board.entity.vo.BoardCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class BoardInfo {
    @Schema(description = "게시물 번호")
    private Long boardId;

    private BoardCategory boardCategory;

    @Schema(description = "글 제목")
    private String title;

    @Schema(description = "작성자")
    private String writer;

    @Schema(description = "날짜")
    private String date;
}
