package com.ay.exchange.board.repository.querydsl;


import com.ay.exchange.board.dto.response.BoardContentResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardContentQueryRepository {
    BoardContentResponse findBoardContent(Long boardId, Pageable page);
}