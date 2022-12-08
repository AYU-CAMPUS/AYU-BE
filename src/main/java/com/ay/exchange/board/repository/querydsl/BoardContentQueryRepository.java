package com.ay.exchange.board.repository.querydsl;


import com.ay.exchange.board.dto.response.BoardContentResponse;
import org.springframework.data.domain.Pageable;

public interface BoardContentQueryRepository {
    BoardContentResponse findBoardContent(Long boardId, Pageable page, String userId);
    Boolean checkModifiableBoard(String userId, Long boardId);

    Boolean canDeleted(String userId, Long boardId);
}