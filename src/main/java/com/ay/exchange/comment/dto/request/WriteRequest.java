package com.ay.exchange.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WriteRequest {
    @Schema(description ="게시글 고유 식별 번호")
    private Long boardId;

    @Schema(description ="글내용")
    private String content;

    @Schema(description = "댓글인지 대댓글인지 구분 (false:댓글 / true:대댓글)")
    private Boolean depth;

    @Schema(description = "대댓글일때 부모 댓글 번호(=댓글 고유 식별 번호)")
    private Long groupId;
}
