package com.ay.exchange.board.dto.query;

import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.BoardContent;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@NoArgsConstructor
@Getter
@Setter
public class BoardContentInfoDto {
    private Long commentId;
    private String writer;
    private String content;
    private Boolean depth;
    private Long groupId;
    private String createdDate;
    private BoardContent boardContent;
    private Board board;
    private Integer exchangeType;
    private String profileImage;

    @QueryProjection
    public BoardContentInfoDto(Long commentId, String writer, String content, Boolean depth, Long groupId, String createdDate, BoardContent boardContent, Board board, Integer exchangeType, String profileImage) {
        this.commentId = commentId;
        this.writer = writer;
        this.content = content;
        this.depth = depth;
        this.groupId = groupId;
        this.createdDate = createdDate;
        this.boardContent = boardContent;
        this.board = board;
        this.exchangeType = exchangeType;
        this.profileImage = profileImage;
    }

}