package com.ay.exchange.comment.dto;

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
    private String profileImage;

    public CommentInfoDto(String writer, String content, Boolean depth, Long groupId, String createdDate, String profileImage) {
        this.writer = writer;
        this.content = content;
        this.depth = depth;
        this.groupId = groupId;
        this.createdDate = createdDate;
        this.profileImage=profileImage;
    }
}
