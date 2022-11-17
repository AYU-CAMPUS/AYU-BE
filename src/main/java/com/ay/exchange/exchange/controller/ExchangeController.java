package com.ay.exchange.exchange.controller;

import com.ay.exchange.board.exception.NotFoundBoardException;
import com.ay.exchange.exchange.service.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exchange")
public class ExchangeController {
    private final ExchangeService exchangeService;

    @GetMapping("/request/{boardId}")
    public Boolean requestExchange(
            @PathVariable("boardId") Long boardId,
            @RequestHeader(value = "token") String token
    ) {
        return exchangeService.requestExchange(boardId, token);
    }
}