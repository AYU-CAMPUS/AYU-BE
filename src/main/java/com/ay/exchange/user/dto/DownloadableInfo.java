package com.ay.exchange.user.dto;

import com.ay.exchange.board.entity.vo.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class DownloadableInfo {
    @Schema(description = "교환일")
    private String exchangeDate;

    @Schema(description = "자료명")
    private String title;

    @Schema(description = "작성자")
    private String writer;

    @Schema(description = "요청자 게시글 번호")
    private Long requesterBoardId;

    @Schema(description = "카테고리")
    private Category category;
}
