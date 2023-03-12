package com.ay.exchange.report.controller;

import com.ay.exchange.common.error.dto.ErrorDto;
import com.ay.exchange.report.dto.request.ReportBoardRequest;
import com.ay.exchange.report.dto.request.ReportCommentRequest;
import com.ay.exchange.report.facade.ReportFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@Tag(name = "신고", description = "신고 관련 api")
public class ReportController {
    private final ReportFacade reportFacade;

    @Operation(summary = "게시글 신고",
            description = "게시글 신고",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "422", description = "이미 신고가 접수되었거나 오류로 신고에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    @PostMapping("/board")
    public Boolean reportBoard(
            @RequestBody ReportBoardRequest reportBoardRequest,
            @CookieValue("token") String token
    ) {
        reportFacade.reportBoard(reportBoardRequest, token);
        return true;
    }

    @Operation(summary = "댓글 신고",
            description = "댓글 신고",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "422", description = "이미 신고가 접수되었거나 오류로 신고에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    @PostMapping("/comment")
    public Boolean reportComment(
            @RequestBody ReportCommentRequest reportCommentRequest,
            @CookieValue("token") String token
    ) {
        reportFacade.reportComment(reportCommentRequest, token);
        return true;
    }

}
