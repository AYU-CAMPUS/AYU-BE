package com.ay.exchange.user.controller;

import com.ay.exchange.user.dto.request.SignInRequest;
import com.ay.exchange.user.dto.request.SignUpRequest;
import com.ay.exchange.user.dto.response.SignInResponse;
import com.ay.exchange.user.dto.response.SignUpResponse;
import com.ay.exchange.user.dto.response.VerificationCodeResponse;
import com.ay.exchange.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "회원가입을 위한 이메일 인증번호 요청"
            , description = "회원가입을 위한 학교 웹메일 인증 "
            , parameters = {@Parameter(name = "email", description = "학교 웹메일")}
    )
    @GetMapping("/sign-up/verification-code")
    public ResponseEntity<VerificationCodeResponse> getVerificationCodeForSignUp(
            @RequestParam("email") String email
    ) {
        return ResponseEntity.ok(userService.getVerificationCodeForSignUp(email));
    }

    @Operation(summary = "회원가입을 위한 인증번호 확인"
            , description = "회원가입을 위한 인증번호 확인"
            , parameters = {@Parameter(name = "number", description = "사용자가 입력한 인증번호")
            , @Parameter(name = "verificationCode", description = "서버에서 제공된 인증번호 토큰")}
    )

    @PostMapping("/sign-up/confirm/verification-code")
    public ResponseEntity<Boolean>confirmVerificationForSignUp(
            @RequestParam("number") String number
            , @RequestHeader("verificationCode") String verificationCode //validate 걸어야 된다.
    ){
        return ResponseEntity.ok(userService.confirmVerificationCode(0, number,verificationCode)); //selection 하드코딩 추후 리팩토링 필요
    }


    @Operation(summary = "비밀번호 찾기를 위한 이메일 인증번호 요청"
            , description = "비밀번호 찾기 시 인증번호 제공"
            , parameters = {@Parameter(name = "email", description = "학교 웹메일")}
    )
    @GetMapping("/find-password/verification-code")
    public ResponseEntity<VerificationCodeResponse> getVerificationCodeForPW(
            @RequestParam("email") String email
    ) {
        return ResponseEntity.ok(userService.getVerificationCodeForPW(email));
    }

    @Operation(summary = "비밀번호 찾기를 위한 인증번호 확인"
            , description = "비밀번호 찾기를 위한 인증번호 확인"
            , parameters = {@Parameter(name = "number", description = "사용자가 입력한 인증번호")
            , @Parameter(name = "verificationCode", description = "서버에서 제공된 인증번호 토큰")}
    )
    @PostMapping("/find-password/confirm/verification-code")
    public ResponseEntity<Boolean>confirmVerificationForPW(
            @RequestParam("number") String number
            , @RequestHeader("verificationCode") String verificationCode //validate 걸어야 된다.
    ){
        return ResponseEntity.ok(userService.confirmVerificationCode(1, number,verificationCode)); //selection 하드코딩 추후 리팩토링 필요
    }

    @Operation(summary = "임시 비밀번호 요청"
            , description = "인증번호 인증 성공 시 임시 비밀번호 제공"
            , parameters = {@Parameter(name = "number", description = "사용자가 입력한 인증번호")
            , @Parameter(name = "verificationCode", description = "서버에서 제공된 인증번호 토큰")}
    )
    @GetMapping("/temporary-password")
    public ResponseEntity<String> getTemporaryPassword(
            @RequestParam("number") String number
            , @RequestHeader("verificationCode") String verificationCode
    ) {
        return ResponseEntity.ok(userService.getTemporaryPassword(number, verificationCode));
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

    @Operation(summary = "중복 닉네임 확인"
            , description = "회원가입 시 중뵥 학교 닉네임인지 확인"
            , parameters = {@Parameter(name = "nickName", description = "유저 닉네임")}
    )
    @GetMapping("/existence-nickname")
    public ResponseEntity<Boolean> existsNickName(
            @RequestParam("nickName") String nickName
    ) {
        return ResponseEntity.ok(userService.checkExistsNickName(nickName));
    }

    @Operation(summary = "아이디 찾기"
            , description = "학교 웹메일로 아이디 찾기"
            , parameters = {@Parameter(name = "email", description = "학교 웹메일")}
    )
    @GetMapping("/find-id")
    public ResponseEntity<String> findUserIdByEmail(
            @RequestParam("email") String email
    ) {
        return ResponseEntity.ok(userService.findUserIdByEmail(email));
    }

    @GetMapping("/slack")
    public String good(){
        throw new RuntimeException("slack error");
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

