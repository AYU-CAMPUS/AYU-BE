package com.ay.exchange.exchange.service;

import com.ay.exchange.exchange.entity.Exchange;
import com.ay.exchange.exchange.exception.UnableExchangeException;
import com.ay.exchange.exchange.repository.ExchangeRepository;
import com.ay.exchange.exchange.repository.querydsl.ExchangeQueryRepository;
import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final JwtTokenProvider jwtTokenProvider;
    private final ExchangeQueryRepository exchangeQueryRepository;
    private final ExchangeRepository exchangeRepository;

    public Boolean requestExchange(Long boardId, String token) {
        String userId = jwtTokenProvider.getUserId(token);
        try {
            exchangeRepository.save(Exchange.builder()
                    .boardId(boardId)
                    .userId(userId)
                    .type(1).build());
        } catch (Exception e) {
            throw new UnableExchangeException();
        }
        return true;
    }
}