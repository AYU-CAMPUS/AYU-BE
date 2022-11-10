package com.ay.exchange.board.dto.response;

import com.ay.exchange.board.entity.vo.BoardCategory;
import com.ay.exchange.comment.dto.CommentInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class BoardContentResponse {
    @Schema(description = "댓글 전체 페이지 수")
    private Long commentPages;

    @Schema(description = "댓글 목록")
    private List<CommentInfoDto> commentList;

    @Schema(description = "글 내용")
    private String content;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "작성자")
    private String writer;

    @Schema(description = "카테고리")
    private BoardCategory boardCategory;

    @Schema(description = "조회수")
    private Integer views;

    @Schema(description = "파일 페이지 수")
    private Integer numberOfFilePages;

    @Schema(description = "교환 완료 수")
    private Integer numberOfSuccessfulExchanges;

    @Schema(description = "작성일")
    private String createdDate;

    @Schema(description = "교환 정보 0: 신청 / 1: 진행중 / 2: 완료")
    private Integer exchangeType;
}
