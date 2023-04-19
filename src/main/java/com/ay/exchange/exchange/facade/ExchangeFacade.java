package com.ay.exchange.exchange.facade;

import com.ay.exchange.exchange.dto.request.ExchangeRequest;
import com.ay.exchange.exchange.dto.response.exchangeMyDataResponse;
import com.ay.exchange.exchange.service.ExchangeService;
import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExchangeFacade {
    private final ExchangeService exchangeService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(rollbackFor = Exception.class)
    public void requestExchange(ExchangeRequest exchangeRequest, String token) {
        exchangeService.existsExchangeCompletion(exchangeRequest);

        String email = jwtTokenProvider.getUserEmail(token);

        exchangeService.requestExchange(exchangeRequest, email);
    }

    public exchangeMyDataResponse getMyData(Integer page, String token) {
        return exchangeService.getMyData(page, jwtTokenProvider.getUserEmail(token));
    }
}
