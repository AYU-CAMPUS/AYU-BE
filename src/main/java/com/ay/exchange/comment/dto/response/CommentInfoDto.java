package com.ay.exchange.comment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentInfoDto {
    @Schema(description = "댓글 고유 번호")
    private Long commentId;

    @Schema(description = "작성자 닉네임")
    private String writer;

    @Schema(description = "댓글 내용")
    private String content;

    @Schema(description = "댓글인지 대댓글인지 구분 (false:댓글 / true:대댓글)")
    private Boolean depth;

    @Schema(description = "대댓글일때 부모 댓글 번호(=댓글 고유 식별 번호)")
    private Long groupId;

    @Schema(description = "댓글 작성 날짜")
    private String createdDate;

    @Schema(description = "작성자 프로필")
    private String profileImage;

}