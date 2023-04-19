package com.ay.exchange.exchange.service;

import com.ay.exchange.board.exception.FailModifyBoardException;
import com.ay.exchange.common.util.DateUtil;
import com.ay.exchange.common.util.PagingGenerator;
import com.ay.exchange.exchange.dto.request.ExchangeRequest;
import com.ay.exchange.exchange.dto.response.ExchangeResponse;
import com.ay.exchange.exchange.dto.response.exchangeMyDataResponse;
import com.ay.exchange.exchange.exception.UnableExchangeException;
import com.ay.exchange.exchange.repository.querydsl.ExchangeQueryRepository;

import com.ay.exchange.user.dto.request.ExchangeAccept;
import com.ay.exchange.user.dto.request.ExchangeRefusal;
import com.ay.exchange.user.exception.FailAcceptFileException;
import com.ay.exchange.user.exception.FailRefusalFileException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final ExchangeQueryRepository exchangeQueryRepository;

    public void requestExchange(ExchangeRequest exchangeRequest, String email, String boardOwnerEmail) {
        int successExchangeCount = 0;
        try { //중복된 자료일 경우 UniQue 제약조건에 의해 MySQLIntegrityConstraintViolationException 발생한다. 422 예외코드를 넘기자.
            successExchangeCount = exchangeQueryRepository.requestExchange(exchangeRequest, boardOwnerEmail, email, DateUtil.getCurrentDate());
        } catch (Exception e) {
            throw new UnableExchangeException();
        }

        if (successExchangeCount != 2) {
            throw new UnableExchangeException();
        }

    }

    public exchangeMyDataResponse getMyData(Integer page, String email) {
        return exchangeQueryRepository.getMyData(PagingGenerator.getPageRequest(page), email);
    }

    public void checkExchangeDate(String date, Long boardId) {
        boolean isExchangeDatePassed3Days = exchangeQueryRepository.checkExchangeDate(date, boardId);
        if (isExchangeDatePassed3Days) {
            return;
        }
        throw new FailModifyBoardException();
    }

    public void refuseExchange(ExchangeRefusal exchangeRefusal, String email) {
        Long deletedExchangeCount = exchangeQueryRepository.refuseExchange(exchangeRefusal, email);
        if (deletedExchangeCount != 2L) { //교환 목록 삭제
            throw new FailRefusalFileException();
        }
    }

    public void deleteExchange(ExchangeAccept exchangeAccept) {
        Long deletedExchangeCount = exchangeQueryRepository.deleteExchange(exchangeAccept);
        if (deletedExchangeCount != 2L) { //교환목록을 삭제
            throw new FailAcceptFileException();
        }
    }

    public ExchangeResponse getExchanges(Integer page, String email) {
        return exchangeQueryRepository.getExchanges(PagingGenerator.getPageRequest(page), email);
    }
}