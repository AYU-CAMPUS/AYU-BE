package com.ay.exchange.exchange.service;

import com.ay.exchange.common.util.DateUtil;
import com.ay.exchange.exchange.dto.request.ExchangeRequest;
import com.ay.exchange.exchange.dto.response.ExchangeResponse;
import com.ay.exchange.exchange.exception.UnableExchangeException;
import com.ay.exchange.exchange.repository.querydsl.ExchangeQueryRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final ExchangeQueryRepository exchangeQueryRepository;

    public void requestExchange(ExchangeRequest exchangeRequest, String email) {
        String boardUserEmail = exchangeQueryRepository.findBoardUserEmail(exchangeRequest.getBoardId(), email);
        if (boardUserEmail == null) {
            throw new UnableExchangeException();
        }

        boolean isRequesterBoard = exchangeQueryRepository.existsRequesterBoard(exchangeRequest.getRequesterBoardId(), email);
        if (!isRequesterBoard) {
            throw new UnableExchangeException();
        }

        int successExchangeCount = exchangeQueryRepository.requestExchange(exchangeRequest, boardUserEmail, email, DateUtil.getCurrentDate());
        if (successExchangeCount != 2) {
            throw new UnableExchangeException();
        }
    }

    public ExchangeResponse getMyData(Integer page, String email) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? (page - 1) : 0, 2,
                Sort.by(Sort.Direction.DESC, "id"));
        return exchangeQueryRepository.getMyData(pageRequest, email);
    }

    public void existsExchangeCompletion(ExchangeRequest exchangeRequest) {
        if(exchangeQueryRepository.existsExchangeCompletion(exchangeRequest)){
            throw new UnableExchangeException();
        }
    }

    public void existsExchange(ExchangeRequest exchangeRequest, String email) {
        if(exchangeQueryRepository.existsExchange(exchangeRequest, email)){
            throw new UnableExchangeException();
        }
    }
}