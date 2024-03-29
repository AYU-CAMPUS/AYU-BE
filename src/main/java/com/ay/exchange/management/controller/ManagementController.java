package com.ay.exchange.management.controller;

import com.ay.exchange.common.error.dto.ErrorDto;
import com.ay.exchange.management.dto.request.BoardIdRequest;
import com.ay.exchange.management.dto.request.SuspensionRequest;
import com.ay.exchange.management.dto.response.RequestBoardResponse;
import com.ay.exchange.management.dto.response.UserInfoResponse;
import com.ay.exchange.management.facade.ManagementFacade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/management")
@Tag(name = "관리자", description = "관리자 관련 api")
public class ManagementController {
    private final ManagementFacade managementFacade;

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
            @CookieValue("token") String token
    ) {
        return managementFacade.findRequestBoard(page);
    }

    @Operation(summary = "요청 게시글 허가", description = "요청 게시글 허가",
            parameters = {
                    @Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "422", description = "게시글 허가에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))}
    )
    @PostMapping("/request-board")
    @PreAuthorize("@Permission.isManager(#token)")
    public Boolean acceptRequestBoard(
            @RequestBody BoardIdRequest boardIdRequest,
            @CookieValue("token") String token
    ) {
        managementFacade.acceptRequestBoard(boardIdRequest);
        return true;
    }

    @Operation(summary = "요청 게시글 거절", description = "요청 게시글 거절",
            parameters = {
                    @Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "422", description = "게시글 거절에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))}
    )
    @DeleteMapping("/request-board")
    @PreAuthorize("@Permission.isManager(#token)")
    public Boolean rejectRequestBoard(
            @RequestBody BoardIdRequest boardIdRequest,
            @CookieValue("token") String token
    ) {
        managementFacade.rejectRequestBoard(boardIdRequest);
        return true;
    }

    @Operation(summary = "정지 주기", description = "사용자 정지 주기",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "422", description = "정지 요청 실패", content = @Content(schema = @Schema(implementation = ErrorDto.class)))}
    )
    @PatchMapping("/suspension")
    @PreAuthorize("@Permission.isManager(#token)")
    public Boolean updateSuspension(
            @RequestBody SuspensionRequest suspensionRequest,
            @CookieValue("token") String token
    ) {
        managementFacade.updateSuspension(suspensionRequest);
        return true;
    }

    @Operation(summary = "유저 정보 조회", description = "유저 정보 조회",
            parameters = {@Parameter(name = "page", description = "페이지 번호"),
                    @Parameter(name = "token", description = "액세스 토큰")}
    )
    @GetMapping("/user")
    public UserInfoResponse getUserInfos(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @CookieValue("token") String token
    ) {
        return managementFacade.getUserInfos(page);
    }
}