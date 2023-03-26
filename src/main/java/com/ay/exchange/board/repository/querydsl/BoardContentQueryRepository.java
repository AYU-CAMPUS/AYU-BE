package com.ay.exchange.board.repository.querydsl;

import com.ay.exchange.board.dto.query.BoardContentInfo2Dto;
import com.ay.exchange.board.dto.query.BoardContentInfoDto;
import com.ay.exchange.board.dto.response.ModifiableBoardResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardContentQueryRepository {
    ModifiableBoardResponse findModifiableBoard(String email, Long boardId);
    BoardContentInfo2Dto findBoardContentWithNoComments(Long boardId, Pageable pageable, String email);
    List<BoardContentInfoDto> findBoardContentWithComments(Long boardId, Pageable pageable, String email);
}