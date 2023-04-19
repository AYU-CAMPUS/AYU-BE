package com.ay.exchange.exchange.facade;

import com.ay.exchange.board.service.BoardService;
import com.ay.exchange.exchange.dto.request.ExchangeRequest;
import com.ay.exchange.exchange.dto.response.exchangeMyDataResponse;
import com.ay.exchange.exchange.exception.UnableExchangeException;
import com.ay.exchange.exchange.service.ExchangeCompletionService;
import com.ay.exchange.exchange.service.ExchangeService;
import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExchangeFacade {
    private final ExchangeService exchangeService;
    private final ExchangeCompletionService exchangeCompletionService;
    private final BoardService boardService;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(rollbackFor = Exception.class)
    public void requestExchange(ExchangeRequest exchangeRequest, String token) {
        String email = jwtTokenProvider.getUserEmail(token);

        String boardOwnerEmail = boardService.findBoardOwnerEmail(exchangeRequest.getBoardId(), email);

        if(email.equals(boardOwnerEmail)){
            throw new UnableExchangeException(); //자기 자신의 자료를 요청하는 행위
        }

        boardService.isBoardOwner(email, exchangeRequest.getRequesterBoardId());

        exchangeCompletionService.existsExchangeCompletion(exchangeRequest, email, boardOwnerEmail);

        exchangeService.requestExchange(exchangeRequest, email, boardOwnerEmail);
    }

    public exchangeMyDataResponse getMyData(Integer page, String token) {
        return exchangeService.getMyData(page, jwtTokenProvider.getUserEmail(token));
    }
}
