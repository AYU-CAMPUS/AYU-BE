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
    private Long exchangeType;
    private String profileImage;
    private String nickName;
    private String desiredData;

    @QueryProjection
    public BoardContentInfoDto(Long commentId, String writer, String content, Boolean depth,
                               Long groupId, String createdDate, BoardContent boardContent, Board board,
                               Long exchangeType, String profileImage, String nickName, String desiredData) {
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
        this.nickName = nickName;
        this.desiredData = desiredData;
    }

}