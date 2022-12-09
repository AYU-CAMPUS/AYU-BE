package com.ay.exchange.board.dto.response;

import com.ay.exchange.board.entity.vo.BoardCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ModifiableBoardResponse {
    @Schema(description = "제목")
    private String title;

    @Schema(description = "카테고리")
    private BoardCategory boardCategory;

    @Schema(description = "파일 페이지 수")
    private Integer numberOfFilePages;

    @Schema(description = "파일명")
    private String originalFileName;

    @Schema(description = "글 내용")
    private String content;
}
