package com.ay.exchange.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentInfoDto {
    private String writer;
    private String content;
    private Boolean depth;
    private Long groupId;
    private String createdDate;
    private String profileImage;

}