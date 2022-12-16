package com.ay.exchange.management.controller;

import com.ay.exchange.common.error.dto.ErrorDto;
import com.ay.exchange.management.dto.request.BoardIdRequest;
import com.ay.exchange.management.dto.response.RequestBoardResponse;
import com.ay.exchange.management.service.ManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/management")
public class ManagementController {
    private final ManagementService managementService;

    @Operation(summary = "요청 게시글 조회", description = "요청 게시글 조회",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호"),
                    @Parameter(name = "token", description = "액세스 토큰")
            }
    )
    @GetMapping("/request-board")
    @PreAuthorize("@Permission.isManager(#token)")
    public RequestBoardResponse requestBoardList(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestHeader("token") String token
    ) {
        return managementService.findRequestBoard(page);
    }

    @Operation(summary = "요청 게시글 허가", description = "요청 게시글 허가",
            parameters = {
                    @Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "422", description = "게시글 허가에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))}
    )
    @PostMapping("/request-board/approval")
    @PreAuthorize("@Permission.isManager(#token)")
    public Boolean acceptRequestBoard(
            @RequestBody BoardIdRequest boardIdRequest,
            @RequestHeader("token") String token
    ) {
        managementService.acceptRequestBoard(boardIdRequest);
        return true;
    }

    @Operation(summary = "요청 게시글 거절", description = "요청 게시글 거절",
            parameters = {
                    @Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "422", description = "게시글 거절에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))}
    )
    @DeleteMapping("/request-board/rejection")
    @PreAuthorize("@Permission.isManager(#token)")
    public Boolean rejectRequestBoard(
            @RequestBody BoardIdRequest boardIdRequest,
            @RequestHeader("token") String token
    ) {
        managementService.rejectRequestBoard(boardIdRequest);
        return true;
    }
}