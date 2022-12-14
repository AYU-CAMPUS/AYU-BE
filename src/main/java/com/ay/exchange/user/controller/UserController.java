package com.ay.exchange.user.controller;

import com.ay.exchange.aws.service.AwsS3Service;
import com.ay.exchange.common.error.dto.ErrorDto;
import com.ay.exchange.common.util.FileValidator;
import com.ay.exchange.user.dto.request.ExchangeRefusal;
import com.ay.exchange.user.dto.request.ExchangeAccept;
import com.ay.exchange.user.dto.request.UserInfoRequest;
import com.ay.exchange.user.dto.response.*;
import com.ay.exchange.user.exception.FailUpdateProfileException;
import com.ay.exchange.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.text.ParseException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Tag(name = "유저", description = "유저(마이페이지) 관련 api")
public class UserController {
    private final UserService userService;
    private final AwsS3Service awsS3Service;

    @Operation(summary = "로그인 성공 시 사용자에 대해 필요한 정보 조회",
            description = "정상적인 사용자면 페이지 상단에 교환 수를 조회, 정지회원이면 정지기간과 정지사유가 있음",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = LoginNotificationResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            })
    @GetMapping("/notification")
    public LoginNotificationResponse searchLoginInfo(HttpServletResponse response, @CookieValue(value = "token") String token) throws ParseException {
        return userService.getUserNotification(response, token);
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

    @Operation(summary = "마이페이지 조회",
            description = "마이페이지 첫 화면",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")}
    )
    @GetMapping("")
    public MyPageResponse getMyPage(
            @CookieValue("token") String token
    ) {
        return userService.getMypage(token);
    }

    @Operation(summary = "내 정보 변경",
            description = "내 정보 변경",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ByteArrayResource.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 형식의 입력입니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "409", description = "이미 존재하는 닉네임 입니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "422", description = "정보 변경에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))}
    )
    @PatchMapping("/info")
    public Boolean updateUserInfo(
            @RequestBody @Valid UserInfoRequest userInfoRequest,
            @CookieValue("token") String token
    ) {
        userService.updateUserInfo(userInfoRequest, token);
        return true;
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
            @CookieValue("token") String token
    ) {
        return userService.getMyData(page, token);
    }

    @Operation(summary = "다운로드 가능한 자료 조회",
            description = "다운로드 가능한 자료 조회",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호"),
                    @Parameter(name = "token", description = "액세스 토큰")
            }
    )
    @GetMapping("/downloadable")
    public DownloadableResponse getDownloadable(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @CookieValue("token") String token
    ) {
        return userService.getDownloadable(page, token);
    }

    @Operation(summary = "자료 다운로드",
            description = "자료 다운로드",
            parameters = {
                    @Parameter(name = "requesterBoardId", description = "요청자 게시물 번호"),
                    @Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ByteArrayResource.class))),
                    @ApiResponse(responseCode = "409", description = "파일이 존재하지 않거나 올바른 사용자가 아닙니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "404", description = "파일이 없습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))}
    )
    @GetMapping(value = "/download/{requesterBoardId}")
    public ResponseEntity<ByteArrayResource> downloadFile(
            @PathVariable("requesterBoardId") Long requesterBoardId,
            @CookieValue("token") String token
    ) {
        //tkddls8900/김상인파일_1666970104756.txt
        //bpax7m4BI/김상인파일.txt
        String filePath = userService.getFilePath(requesterBoardId, token);

        byte[] data = awsS3Service.downloadFile(filePath);
        ByteArrayResource resource = new ByteArrayResource(data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(data.length);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(AwsS3Service.createContentDisposition(filePath));

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(resource);
    }

    @Operation(summary = "교환신청 조회",
            description = "교환신청 조회",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호"),
                    @Parameter(name = "token", description = "액세스 토큰")}
    )
    @GetMapping("/exchange")
    public ExchangeResponse getExchanges(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @CookieValue("token") String token
    ) {
        return userService.getExchanges(page, token);
    }

    @Operation(summary = "교환신청 수락",
            description = "교환신청 수락",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ByteArrayResource.class))),
                    @ApiResponse(responseCode = "422", description = "교환 수락에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))}
    )
    @PostMapping("/exchange")
    public Boolean acceptExchange(
            @RequestBody ExchangeAccept exchangeAccept,
            @CookieValue("token") String token
    ) {
        return userService.acceptExchange(exchangeAccept, token);
    }

    @Operation(summary = "교환신청 거절",
            description = "교환신청 거절",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ByteArrayResource.class))),
                    @ApiResponse(responseCode = "422", description = "교환 거절에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))}
    )
    @DeleteMapping("/exchange")
    public Boolean refuseExchange(
            @RequestBody ExchangeRefusal exchangeRefusal,
            @CookieValue("token") String token
    ) {
        return userService.refuseExchange(exchangeRefusal, token);
    }

    @Operation(summary = "프로필 이미지 변경",
            description = "프로필 이미지 변경",
            parameters = {
                    @Parameter(name = "file", description = "이미지"),
                    @Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ByteArrayResource.class))),
                    @ApiResponse(responseCode = "422", description = "프로필 변경에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
                    @ApiResponse(responseCode = "404", description = "파일 업로드에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))}
    )
    @PatchMapping("/profile")
    public Boolean updateProfile(
            @RequestPart("file") MultipartFile multipartFile,
            @CookieValue("token") String token
    ) {
        if (FileValidator.isAllowedImageType(multipartFile)) {
            return userService.updateProfile(multipartFile, token);
        }
        throw new FailUpdateProfileException();
    }

    @Operation(summary = "회원 탈퇴",
            description = "회원 탈퇴",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ByteArrayResource.class))),
                    @ApiResponse(responseCode = "422", description = "회원 탈퇴에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            }
    )
    @DeleteMapping("/withdrawal")
    public Boolean withdrawalUser(
            @CookieValue("token") String token
    ) {
        return userService.withdrawalUser(token);
    }
}