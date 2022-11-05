package com.ay.exchange.comment.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringPath;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CommentInfoDto {
    private String writer;
    private String content;
    private Boolean depth;
    private Long groupId;
    private String createdDate;

    @QueryProjection
    public CommentInfoDto(String writer, String content, Boolean depth, Long groupId, String createdDate) {
        this.writer = writer;
        this.content = content;
        this.depth = depth;
        this.groupId = groupId;
        this.createdDate = createdDate;
    }

}
