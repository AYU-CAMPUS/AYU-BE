package com.ay.exchange.exchange.service;

import com.ay.exchange.board.exception.FailDeleteBoardException;
import com.ay.exchange.board.exception.FailModifyBoardException;
import com.ay.exchange.common.util.DateUtil;
import com.ay.exchange.common.util.PagingGenerator;
import com.ay.exchange.exchange.repository.querydsl.ExchangeCompletionRepository;
import com.ay.exchange.user.dto.request.ExchangeAccept;
import com.ay.exchange.user.dto.response.DownloadableResponse;
import com.ay.exchange.user.exception.FailAcceptFileException;
import com.ay.exchange.user.exception.FailWithdrawalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.ay.exchange.common.util.DateUtil.getAvailableDate;

@Service
@RequiredArgsConstructor
public class ExchangeCompletionService {
    private final ExchangeCompletionRepository exchangeCompletionRepository;

    public void checkDeleteable(String email, Long boardId) {
        boolean isExchangeCompletionDatePassed3Days = exchangeCompletionRepository.checkExchangeCompletionDate(getAvailableDate(), email, boardId);
        if (isExchangeCompletionDatePassed3Days) {
            return;
        }
        throw new FailDeleteBoardException();
    }

    public void checkExchangeCompletionDate(String date, String email, Long boardId) {
        boolean isExchangeCompletionDatePassed3Days = exchangeCompletionRepository.checkExchangeCompletionDate(date, email, boardId);
        if (isExchangeCompletionDatePassed3Days) {
            return;
        }
        throw new FailModifyBoardException();
    }

    public void checkMyPageExchangeCompletionDate(String email){
        boolean isExchangeDatePassed3Days = exchangeCompletionRepository.checkMyPageExchangeCompletionDate(getAvailableDate(), email);
        if (isExchangeDatePassed3Days) { //최근 교환 내역이 3일이 넘었으면 회원 탈퇴 가능
            return;
        }
        throw new FailWithdrawalException();
    }

    public void acceptExchange(ExchangeAccept exchangeAccept, String email) {
        String currentDate = DateUtil.getCurrentDate();
        int successExchangeCount = exchangeCompletionRepository.acceptExchange(currentDate, exchangeAccept, email);
        if (successExchangeCount != 2) {
            throw new FailAcceptFileException();
        }
    }

    public DownloadableResponse getDownloadable(Integer page, String email) {
        return exchangeCompletionRepository.getDownloadable(PagingGenerator.getPageRequest(page), email);
    }

    public int getDownloadableCount(String email) {
        return exchangeCompletionRepository.getDownloadableCount(email).intValue();
    }
}
