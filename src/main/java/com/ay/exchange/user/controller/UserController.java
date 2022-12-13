package com.ay.exchange.user.controller;

import com.ay.exchange.common.error.dto.ErrorDto;
import com.ay.exchange.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Tag(name = "유저", description = "유저 관련 api")
public class UserController {
    private final UserService userService;

    @Operation(summary = "중복 닉네임 확인",
            description = "회원가입 시 중뵥 학교 닉네임인지 확인",
            parameters = {@Parameter(name = "nickName", description = "유저 닉네임")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 형식의 입력입니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    @GetMapping("/existence-nickname")
    public ResponseEntity<Boolean> existsNickName(
            @RequestParam("nickName") @Valid @Pattern(regexp = "^[a-zA-Z\\d가-힣]{1,8}$") String nickName
    ) {
        return ResponseEntity.ok(userService.checkExistsNickName(nickName));
    }

}

