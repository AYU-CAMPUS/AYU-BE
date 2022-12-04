package com.ay.exchange.user.controller;

import com.ay.exchange.common.error.dto.ErrorDto;
import com.ay.exchange.user.dto.request.ResetPasswordRequest;
import com.ay.exchange.user.dto.request.SignInRequest;
import com.ay.exchange.user.dto.request.SignUpRequest;
import com.ay.exchange.user.dto.response.FindIdResponse;
import com.ay.exchange.user.dto.response.SignInResponse;
import com.ay.exchange.user.dto.response.SignUpResponse;
import com.ay.exchange.user.dto.response.VerificationCodeResponse;
import com.ay.exchange.user.exception.NotExistsUserIdException;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Tag(name = "유저", description = "유저 관련 api")
public class UserController {
    private final UserService userService;

    @Operation(summary = "로그인", description = "로그인 요청", responses = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = SignInResponse.class))),
            @ApiResponse(responseCode = "404", description = "아이디 또는 비밀번호가 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(
            @RequestBody @Valid SignInRequest signInRequest
    ) {
        return ResponseEntity.ok(userService.signIn(signInRequest));
    }

    @Operation(summary = "회원가입", description = "회원가입 요청", responses = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = SignUpResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일입니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
    })
    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(
            @RequestBody @Valid SignUpRequest signUpRequest
    ) {
        return ResponseEntity.ok(userService.signUp(signUpRequest));
    }

    @Operation(summary = "회원가입을 위한 인증번호 요청",
            description = "회원가입을 위한 학교 웹메일 인증 ",
            parameters = {@Parameter(name = "email", description = "학교 웹메일")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = VerificationCodeResponse.class))),
                    @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일입니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    @GetMapping("/sign-up/verification-code")
    public ResponseEntity<VerificationCodeResponse> getVerificationCodeForSignUp(
            @RequestParam("email") @Valid @Pattern(regexp = "^[a-zA-Z\\d-_.]{3,30}$") String email
    ) {
        return ResponseEntity.ok(userService.getVerificationCodeForSignUp(email));
    }

    @Operation(summary = "회원가입을 위한 인증번호 확인",
            description = "회원가입을 위한 인증번호 확인",
            parameters = {
                    @Parameter(name = "number", description = "사용자가 입력한 인증번호"),
                    @Parameter(name = "verificationCode", description = "서버에서 제공된 인증번호 토큰")
            }
    )
    @PostMapping("/sign-up/confirm/verification-code")
    public ResponseEntity<Boolean> confirmVerificationForSignUp(
            @RequestParam("number") @Valid @NotBlank String number,
            @RequestHeader("verificationCode") @Valid @NotBlank String verificationCode //validate 걸어야 된다.
    ) {
        System.out.println(number);
        return ResponseEntity.ok(userService.confirmVerificationCode(0, number, verificationCode)); //selection 하드코딩 추후 리팩토링 필요
    }

//    @Operation(summary = "비밀번호 찾기를 위한 이메일 인증번호 요청",
//            description = "비밀번호 찾기 시 인증번호 제공",
//            parameters = {@Parameter(name = "email", description = "학교 웹메일")},
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = VerificationCodeResponse.class))),
//                    @ApiResponse(responseCode = "404", description = "이메일이 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
//            }
//    )
//    @GetMapping("/find-password/verification-code")
//    public ResponseEntity<VerificationCodeResponse> getVerificationCodeForID(
//            @RequestParam("email") @Valid @Pattern(regexp = "^[a-zA-Z\\d-_.]{3,30}$") String email
//    ) {
//        return ResponseEntity.ok(userService.getVerificationCodeForPW(email));
//    }
//
//    @Operation(summary = "비밀번호 찾기를 위한 인증번호 확인",
//            description = "비밀번호 찾기를 위한 인증번호 확인",
//            parameters = {
//                    @Parameter(name = "number", description = "사용자가 입력한 인증번호"),
//                    @Parameter(name = "verificationCode", description = "서버에서 제공된 인증번호 토큰")
//            }
//    )
//    @PostMapping("/find-password/confirm/verification-code")
//    public ResponseEntity<Boolean> confirmVerificationForPW(
//            @RequestParam("number") @Valid @NotBlank String number,
//            @RequestHeader("verificationCode") @Valid @NotBlank String verificationCode //validate 걸어야 된다.
//    ) {
//        return ResponseEntity.ok(userService.confirmVerificationCode(1, number, verificationCode)); //selection 하드코딩 추후 리팩토링 필요
//    }

    @Operation(summary = "아이디 찾기를 위한 이메일 인증번호 요청",
            description = "아이디 찾기 시 인증번호 제공",
            parameters = {@Parameter(name = "email", description = "학교 웹메일")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = VerificationCodeResponse.class))),
                    @ApiResponse(responseCode = "404", description = "이메일이 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    @GetMapping("/find-id/verification-code")
    public ResponseEntity<VerificationCodeResponse> getVerificationCodeForPW(
            @RequestParam("email") @Valid @Pattern(regexp = "^[a-zA-Z\\d-_.]{3,30}$") String email
    ) {
        return ResponseEntity.ok(userService.getVerificationCodeForPW(email));
    }

    @Operation(summary = "아이디 찾기를 위한 인증번호 확인",
            description = "아이디 찾기를 위한 인증번호 확인",
            parameters = {
                    @Parameter(name = "number", description = "사용자가 입력한 인증번호"),
                    @Parameter(name = "verificationCode", description = "서버에서 제공된 인증번호 토큰")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = FindIdResponse.class))),
                    @ApiResponse(responseCode = "404", description = "이메일이 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "412", description = "인증에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    @PostMapping("/find-id/confirm/verification-code")
    public ResponseEntity<FindIdResponse> confirmVerificationForID(
            @RequestParam("number") @Valid @NotBlank String number,
            @RequestHeader("verificationCode") @Valid @NotBlank String verificationCode //validate 걸어야 된다.
    ) {
        return ResponseEntity.ok(userService.findUserId(number, verificationCode)); //selection 하드코딩 추후 리팩토링 필요
    }

//    @Operation(summary = "비밀번호 재설정",
//            description = "비밀번호 재설정",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = Boolean.class))),
//                    @ApiResponse(responseCode = "404", description = "이메일이 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
//            }
//    )
//    @PatchMapping("/reset-password")
//    public ResponseEntity<Boolean> resetPassword(
//            @RequestBody ResetPasswordRequest resetPasswordRequest
//    ) {
//        return ResponseEntity.ok(userService.resetPassword(resetPasswordRequest));
//    }

    @Operation(summary = "중복 아이디 확인",
            description = "회원가입 시 중복 아이디인지 확인",
            parameters = {@Parameter(name = "userId", description = "유저 아이디")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = Boolean.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 형식의 입력입니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    @GetMapping("/existence-id")
    public ResponseEntity<Boolean> existsUserId(
            @RequestParam("userId") @Valid @Pattern(regexp = "^[a-zA-Z\\d]{6,15}$") String userId
    ) {
        return ResponseEntity.ok(userService.checkExistsUserId(userId));
    }

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

