package com.ay.exchange.management.controller;

import com.ay.exchange.management.dto.response.RequestBoardResponse;
import com.ay.exchange.management.service.ManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
}