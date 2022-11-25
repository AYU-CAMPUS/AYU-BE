package com.ay.exchange.exchange.service;

import com.ay.exchange.exchange.dto.request.ExchangeRequest;
import com.ay.exchange.exchange.dto.response.ExchangeResponse;
import com.ay.exchange.exchange.entity.Exchange;
import com.ay.exchange.exchange.exception.UnableExchangeException;
import com.ay.exchange.exchange.repository.ExchangeRepository;
import com.ay.exchange.exchange.repository.querydsl.ExchangeQueryRepository;
import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final JwtTokenProvider jwtTokenProvider;
    private final ExchangeQueryRepository exchangeQueryRepository;

    public Boolean requestExchange(ExchangeRequest exchangeRequest, String token) {
        exchangeQueryRepository.requestExchange(exchangeRequest, jwtTokenProvider.getUserId(token));
        return true;
    }

    public ExchangeResponse getMyData(Integer page, String token) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));
        return exchangeQueryRepository.getMyData(pageRequest, jwtTokenProvider.getUserId(token));
    }
}