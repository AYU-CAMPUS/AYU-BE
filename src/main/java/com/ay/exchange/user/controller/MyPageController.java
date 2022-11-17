package com.ay.exchange.user.controller;

import com.ay.exchange.user.dto.response.MyPageResponse;
import com.ay.exchange.user.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @Operation(summary = "마이페이지 조회"
            , description = "마이페이지 첫 화면"
            , parameters = {@Parameter(name = "token", description = "액세스 토큰")}
    )
    @GetMapping("")
    public MyPageResponse getMyPage(
            @RequestHeader("token") String token
    ){
        return myPageService.getMypage(token);
    }
}