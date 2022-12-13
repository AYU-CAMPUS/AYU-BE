package com.ay.exchange.report.dto.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentInfo {
    private String targetUserEmail;
    private String content;
}