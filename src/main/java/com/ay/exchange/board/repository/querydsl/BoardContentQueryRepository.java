package com.ay.exchange.board.repository.querydsl;

import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.dto.response.ModifiableBoardResponse;
import org.springframework.data.domain.Pageable;

public interface BoardContentQueryRepository {
    BoardContentResponse findBoardContent(Long boardId, Pageable page, String email);
    ModifiableBoardResponse findModifiableBoard(String date, String email, Long boardId);

    Boolean canDeleted(String email, Long boardId);

    Boolean checkExchangeCompletionDate(String date, String email, Long boardId);

    Boolean checkExchangeDate(String date, Long boardId);
}