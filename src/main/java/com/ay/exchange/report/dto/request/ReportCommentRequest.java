package com.ay.exchange.report.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ReportCommentRequest {
    @Schema(description = "신고 댓글 번호")
    private Long commentId;

    @Schema(description = "신고 사유")
    private String reason;
}