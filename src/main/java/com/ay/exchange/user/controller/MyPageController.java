package com.ay.exchange.user.controller;

import com.ay.exchange.user.dto.request.PasswordChangeRequest;
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

    @Operation(summary = "마이페이지 조회"
            , description = "마이페이지 첫 화면"
            , parameters = {@Parameter(name = "token", description = "액세스 토큰")}
    )
    @GetMapping("")
    public MyPageResponse getMyPage(
            @RequestHeader("token") String token
    ) {
        return myPageService.getMypage(token);
    }

    @PatchMapping("/password")
    public Boolean updatePassword(
            @RequestBody @Valid PasswordChangeRequest passwordChangeRequest,
            @RequestHeader("token") String token
    ) {
        return myPageService.updatePassword(passwordChangeRequest, token);
    }
}