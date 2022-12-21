package com.ay.exchange.exchange.controller;

import com.ay.exchange.common.error.dto.ErrorDto;
import com.ay.exchange.exchange.dto.request.ExchangeRequest;
import com.ay.exchange.exchange.dto.response.ExchangeResponse;
import com.ay.exchange.exchange.service.ExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exchange")
@Tag(name = "교환", description = "교환 관련 api")
public class ExchangeController {
    private final ExchangeService exchangeService;

    @Operation(summary = "자료요청", description = "자료요청",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ByteArrayResource.class))),
                    @ApiResponse(responseCode = "422", description = "교환신청에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ErrorDto.class)))}
    )
    @PostMapping("/request")
    public Boolean requestExchange(
            @RequestBody ExchangeRequest exchangeRequest,
            @CookieValue(value = "token") String token
    ) {
        return exchangeService.requestExchange(exchangeRequest, token);
    }

    @Operation(summary = "내 자료 조회", description = "교환 요청 시 내 자료 조회",
            parameters = {
                    @Parameter(name = "page", description = "페이지 번호"),
                    @Parameter(name = "token", description = "액세스 토큰")}
    )
    @GetMapping("")
    public ExchangeResponse getMyData(
            @RequestParam(name = "page", required = false, defaultValue = "1") Integer page,
            @CookieValue(value = "token") String token
    ){
        return exchangeService.getMyData(page, token);
    }
}