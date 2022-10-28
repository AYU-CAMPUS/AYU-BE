package com.ay.exchange.board.dto.request;

import com.ay.exchange.board.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WriteRequest {
    @Schema(description = "글 제목")
    private String title;

    @Schema(description = "카테고리")
    private CategoryDto categoryDto;

    @Schema(description = "파일 페이지 수")
    private Integer numberOfFilePages;

    @Schema(description = "글 내용")
    private String content;
}