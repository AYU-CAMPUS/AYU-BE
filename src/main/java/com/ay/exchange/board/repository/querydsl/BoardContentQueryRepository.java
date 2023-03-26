package com.ay.exchange.board.repository.querydsl;

import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.dto.response.ModifiableBoardResponse;
import org.springframework.data.domain.Pageable;

public interface BoardContentQueryRepository {
    BoardContentResponse findBoardContent(Long boardId, Pageable page, String email);
    ModifiableBoardResponse findModifiableBoard(String email, Long boardId);
}