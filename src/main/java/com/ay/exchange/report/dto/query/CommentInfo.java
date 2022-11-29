package com.ay.exchange.report.dto.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentInfo {
    private String targetUserId;
    private String content;
}