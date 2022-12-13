package com.ay.exchange.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MyDataInfo {
    @Schema(description = "등록일")
    private String createdDate;

    @Schema(description = "자료명")
    private String title;

    @Schema(description = "자료명 게시글 번호")
    private Long boardId;
}
