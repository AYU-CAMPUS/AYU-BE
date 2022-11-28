package com.ay.exchange.mypage.controller;

import com.ay.exchange.aws.service.AwsS3Service;
import com.ay.exchange.mypage.dto.request.ExchangeRefusal;
import com.ay.exchange.mypage.dto.request.ExchangeAccept;
import com.ay.exchange.mypage.dto.response.ExchangeResponse;
import com.ay.exchange.user.dto.request.PasswordChangeRequest;
import com.ay.exchange.mypage.dto.response.DownloadableResponse;
import com.ay.exchange.mypage.dto.response.MyDataResponse;
import com.ay.exchange.mypage.dto.response.MyPageResponse;
import com.ay.exchange.mypage.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private final AwsS3Service awsS3Service;

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
            @RequestHeader("token") String token
    ) {
        return myPageService.getDownloadable(page, token);
    }

    @Operation(summary = "자료 다운로드",
            description = "자료 다운로드",
            parameters = {
                    @Parameter(name = "boardId", description = "게시물 번호"),
                    @Parameter(name = "token", description = "액세스 토큰")
            }
    )
    @GetMapping(value = "/download/{requesterBoardId}")
    public ResponseEntity<ByteArrayResource> downloadFile(
            @PathVariable("requesterBoardId") Long requesterBoardId,
            @RequestHeader("token") String token
    ) {
        //tkddls8900/김상인파일_1666970104756.txt
        //bpax7m4BI/김상인파일.txt
        String filePath = myPageService.getFilePath(requesterBoardId, token);

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
                    @Parameter(name = "token", description = "액세스 토큰")
            }
    )
    @GetMapping("/exchange")
    public ExchangeResponse getExchanges(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @RequestHeader("token") String token
    ) {
        return myPageService.getExchanges(page, token);
    }

    @Operation(summary = "교환신청 수락",
            description = "교환신청 수락",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")}
    )
    @PostMapping("/exchange/accept")
    public Boolean acceptExchange(
            @RequestBody ExchangeAccept exchangeAccept,
            @RequestHeader("token") String token
    ) {
        return myPageService.acceptExchange(exchangeAccept, token);
    }

    @Operation(summary = "교환신청 거절",
            description = "교환신청 거절",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")}
    )
    @DeleteMapping("/exchange/refusal")
    public Boolean refuseExchange(
            @RequestBody ExchangeRefusal exchangeRefusal,
            @RequestHeader("token") String token
    ) {
        return myPageService.refuseExchange(exchangeRefusal, token);
    }

}