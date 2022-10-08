package com.ay.exchange.user.controller;

import com.ay.exchange.user.dto.request.SignInRequest;
import com.ay.exchange.user.dto.request.SignUpRequest;
import com.ay.exchange.user.dto.response.SignInResponse;
import com.ay.exchange.user.dto.response.SignUpResponse;
import com.ay.exchange.user.dto.response.VerificationCodeResponse;
import com.ay.exchange.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "유저", description = "유저 관련 api")
public class UserController {
    private final UserService userService;

    @Operation(summary = "로그인", description = "로그인 요청"
            , requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "signInRequest")
            , responses = {@ApiResponse(description = "SuccessFul", responseCode = "200"
                    , content = @Content(schema = @Schema(implementation = SignInResponse.class)))}
    ) //추후 requestBody는 제거하고 responses는 예외처리 사항에 대해서 추가한다
    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(
            @RequestBody SignInRequest signInRequest
    ) {
        return ResponseEntity.ok(userService.signIn(signInRequest));
    }

    @Operation(summary = "회원가입", description = "회원가입 요청")
    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(
            @RequestBody SignUpRequest signUpRequest
    ) {
        return ResponseEntity.ok(userService.signUp(signUpRequest));
    }

    @Operation(summary = "인증번호 요청"
            , description = "회원가입을 위한 학교 웹메일 인증 or 비밀번호 찾기 시 인증번호 제공"
            , parameters = {@Parameter(name = "email", description = "학교 웹메일")}
    )
    @GetMapping({"/sign-up/verification-code"
            , "/find-password/verification-code"})
    public ResponseEntity<VerificationCodeResponse> getVerificationCode(
            @RequestParam("email") String email
    ) {
        return ResponseEntity.ok(userService.getVerificationCode(email));
    }

    @Operation(summary = "임시 비밀번호 요청"
            , description = "인증번호 인증 성공 시 임시 비밀번호 제공"
            , parameters = {@Parameter(name = "number", description = "사용자가 입력한 인증번호")
            , @Parameter(name = "token", description = "토큰에 서버에서 제공된 인증번호가 있음")}
    )
    @GetMapping("/temporary-password")
    public ResponseEntity<String> getTemporaryPassword(
            @RequestParam("number") String number
            , @RequestHeader("token") String verificationCodeToken
    ) {
        return ResponseEntity.ok(userService
                .getTemporaryPassword(number, verificationCodeToken));
    }

    @Operation(summary = "중복 아이디 확인"
            , description = "회원가입 시 중복 아이디인지 확인"
            , parameters = {@Parameter(name = "userId", description = "유저 아이디")})
    @GetMapping("/existence-id")
    public ResponseEntity<Boolean> existsUserId(
            @RequestParam("userId") String userId
    ) {
        return ResponseEntity.ok(userService.checkExistsUserId(userId));
    }

    @Operation(summary = "중복 이메일 확인"
            , description = "회원가입 시 중뵥 학교 웹메일인지 확인"
            , parameters = {@Parameter(name = "email", description = "학교 웹메일")}
    )
    @GetMapping("/existence-email")
    public ResponseEntity<Boolean> existsEmail(
            @RequestParam("email") String email
    ) {
        return ResponseEntity.ok(userService.checkExistsNickName(email));
    }

    @Operation(summary = "아이디 찾기"
            , description = "학교 웹메일로 아이디 찾기"
            , parameters = {@Parameter(name = "email", description = "학교 웹메일")}
    )
    @GetMapping("/find-id")
    public ResponseEntity<String> findUserId(
            @RequestParam("email") String email
    ) {
        return ResponseEntity.ok(userService.findUserId(email));
    }


//    @PatchMapping("/update-password")
//    public ResponseEntity<Boolean> updatePassword(
//            @RequestBody UpdatePasswordRequest updatePasswordRequest
//    ) {
//        return ResponseEntity.ok(
//                userService.updateUserPassword(updatePasswordRequest)
//        );
//    }

}

