package com.ay.exchange.board.dto.request;

import com.ay.exchange.board.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ModificationRequest {
    @Schema(description = "게시글 번호")
    private Long boardId;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "카테고리")
    private CategoryDto categoryDto;

    @Schema(description = "파일 페이지 수")
    private Integer numberOfFilePages;

    @Schema(description = "글 내용")
    private String content; // board_content join
}