package com.ay.exchange.mypage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DownloadableInfo {
    @Schema(description = "교환일")
    private String exchangeDate;

    @Schema(description = "자료명")
    private String title;

    @Schema(description = "작성자")
    private String writer;

    @Schema(description = "게시글 번호")
    private Long boardId;
}
