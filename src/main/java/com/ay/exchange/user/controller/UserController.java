package com.ay.exchange.user.controller;

import com.ay.exchange.common.error.dto.ErrorDto;
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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "로그인", description = "로그인 요청",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "signInRequest"),
            responses = {@ApiResponse(description = "SuccessFul", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = SignInResponse.class)))}
    ) //추후 requestBody는 제거하고 responses는 예외처리 사항에 대해서 추가한다
    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(
            @RequestBody @Valid SignInRequest signInRequest
    ) {
        return ResponseEntity.ok(userService.signIn(signInRequest));
    }

    @Operation(summary = "회원가입", description = "회원가입 요청")
    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(
            @RequestBody @Valid SignUpRequest signUpRequest
    ) {
        return ResponseEntity.ok(userService.signUp(signUpRequest));
    }

    @Operation(summary = "회원가입을 위한 이메일 인증번호 요청",
            description = "회원가입을 위한 학교 웹메일 인증 ",
            parameters = {@Parameter(name = "email", description = "학교 웹메일")}
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
        return ResponseEntity.ok(userService.confirmVerificationCode(0, number, verificationCode)); //selection 하드코딩 추후 리팩토링 필요
    }


    @Operation(summary = "비밀번호 찾기를 위한 이메일 인증번호 요청",
            description = "비밀번호 찾기 시 인증번호 제공",
            parameters = {@Parameter(name = "email", description = "학교 웹메일")}
    )
    @GetMapping("/find-password/verification-code")
    public ResponseEntity<VerificationCodeResponse> getVerificationCodeForPW(
            @RequestParam("email") @Valid @Pattern(regexp = "^[a-zA-Z\\d-_.]{3,30}$") String email
    ) {
        return ResponseEntity.ok(userService.getVerificationCodeForPW(email));
    }

    @Operation(summary = "비밀번호 찾기를 위한 인증번호 확인",
            description = "비밀번호 찾기를 위한 인증번호 확인",
            parameters = {
                    @Parameter(name = "number", description = "사용자가 입력한 인증번호"),
                    @Parameter(name = "verificationCode", description = "서버에서 제공된 인증번호 토큰")
            }
    )
    @PostMapping("/find-password/confirm/verification-code")
    public ResponseEntity<Boolean> confirmVerificationForPW(
            @RequestParam("number") @Valid @NotBlank String number,
            @RequestHeader("verificationCode") @Valid @NotBlank String verificationCode //validate 걸어야 된다.
    ) {
        return ResponseEntity.ok(userService.confirmVerificationCode(1, number, verificationCode)); //selection 하드코딩 추후 리팩토링 필요
    }

    @Operation(summary = "임시 비밀번호 요청",
            description = "인증번호 인증 성공 시 임시 비밀번호 제공",
            parameters = {
                    @Parameter(name = "number", description = "사용자가 입력한 인증번호"),
                    @Parameter(name = "verificationCode", description = "서버에서 제공된 인증번호 토큰")
            }
    )
    @GetMapping("/temporary-password")
    public ResponseEntity<String> getTemporaryPassword(
            @RequestParam("number") @Valid @NotBlank String number,
            @RequestHeader("verificationCode") @Valid @NotBlank String verificationCode
    ) {
        //잘못된 요청이면 null이 리턴되는데 예외처리를 해야될지 프론트와 상의해봐야함.
        return ResponseEntity.ok(userService.getTemporaryPassword(number, verificationCode));
    }

    @Operation(summary = "중복 아이디 확인",
            description = "회원가입 시 중복 아이디인지 확인",
            parameters = {@Parameter(name = "userId", description = "유저 아이디")})
    @GetMapping("/existence-id")
    public ResponseEntity<Boolean> existsUserId(
            @RequestParam("userId") @Valid @Pattern(regexp = "^[a-zA-Z\\d]{6,15}$") String userId
    ) {
        return ResponseEntity.ok(userService.checkExistsUserId(userId));
    }

    @Operation(summary = "중복 닉네임 확인",
            description = "회원가입 시 중뵥 학교 닉네임인지 확인",
            parameters = {@Parameter(name = "nickName", description = "유저 닉네임")}
    )
    @GetMapping("/existence-nickname")
    public ResponseEntity<Boolean> existsNickName(
            @RequestParam("nickName") @Valid @Pattern(regexp = "^[a-zA-Z\\d가-힣]{1,8}$") String nickName
    ) {
        return ResponseEntity.ok(userService.checkExistsNickName(nickName));
    }

    @Operation(summary = "아이디 찾기",
            description = "학교 웹메일로 아이디 찾기",
            parameters = {@Parameter(name = "email", description = "학교 웹메일")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/find-id")
    public ResponseEntity<String> findUserIdByEmail(
            @RequestParam("email") @Valid @Pattern(regexp = "^[a-zA-Z\\d-_.]{3,30}$") String email
    ) {
        return ResponseEntity.ok(userService.findUserIdByEmail(email));
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

