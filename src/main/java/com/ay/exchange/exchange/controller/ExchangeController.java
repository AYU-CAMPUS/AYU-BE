package com.ay.exchange.exchange.controller;

import com.ay.exchange.board.exception.NotFoundBoardException;
import com.ay.exchange.exchange.service.ExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exchange")
public class ExchangeController {
    private final ExchangeService exchangeService;

    @Operation(summary = "자료요청", description = "자료요청",
            parameters = {
                    @Parameter(name = "boardId", description = "게시글 번호"),
                    @Parameter(name = "token", description = "액세스 토큰")}
    )
    @GetMapping("/request/{boardId}")
    public Boolean requestExchange(
            @PathVariable("boardId") Long boardId,
            @RequestHeader(value = "token") String token
    ) {
        return exchangeService.requestExchange(boardId, token);
    }
}