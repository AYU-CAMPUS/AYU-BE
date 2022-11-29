package com.ay.exchange.report.controller;

import com.ay.exchange.report.dto.request.ReportBoardRequest;
import com.ay.exchange.report.dto.request.ReportCommentRequest;
import com.ay.exchange.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @Operation(summary = "게시글 신고",
            description = "게시글 신고",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")}
    )
    @PostMapping("/board")
    public Boolean reportBoard(
            @RequestBody ReportBoardRequest reportBoardRequest,
            @RequestHeader("token") String token
    ) {
        return reportService.reportBoard(reportBoardRequest, token);
    }

    @Operation(summary = "댓글 신고",
            description = "댓글 신고",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")}
    )
    @PostMapping("/comment")
    public Boolean reportComment(
            @RequestBody ReportCommentRequest reportCommentRequest,
            @RequestHeader("token") String token
    ) {
        return reportService.reportComment(reportCommentRequest, token);
    }

}
