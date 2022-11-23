package com.ay.exchange.user.controller;

import com.ay.exchange.user.dto.request.PasswordChangeRequest;
import com.ay.exchange.user.dto.response.MyDataResponse;
import com.ay.exchange.user.dto.response.MyPageResponse;
import com.ay.exchange.user.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Validated
@Tag(name = "마이페이지", description = "마이페이지 관련 api")
public class MyPageController {
    private final MyPageService myPageService;

    @Operation(summary = "마이페이지 조회",
            description = "마이페이지 첫 화면",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")}
    )
    @GetMapping("")
    public MyPageResponse getMyPage(
            @RequestHeader("token") String token
    ) {
        return myPageService.getMypage(token);
    }

    @Operation(summary = "비밀번호 변경",
            description = "비밀번호 변경",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")}
    )
    @PatchMapping("/password")
    public Boolean updatePassword(
            @RequestBody @Valid PasswordChangeRequest passwordChangeRequest,
            @RequestHeader("token") String token
    ) {
        return myPageService.updatePassword(passwordChangeRequest, token);
    }

    @Operation(summary = "내가 올린 자료 조회",
            description = "내가 올린 자료 조회",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호"),
                    @Parameter(name = "token", description = "액세스 토큰")
            }
    )
    @GetMapping("/data")
    public MyDataResponse getData(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestHeader("token") String token
    ) {
        return myPageService.getMyData(page, token);
    }

}