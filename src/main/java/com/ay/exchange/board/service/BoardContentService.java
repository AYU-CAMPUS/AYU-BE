package com.ay.exchange.board.service;

import com.ay.exchange.board.dto.query.BoardContentInfo2Dto;
import com.ay.exchange.board.dto.query.BoardContentInfoDto;
import com.ay.exchange.board.dto.response.ModifiableBoardResponse;
import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.BoardContent;
import com.ay.exchange.board.repository.BoardContentRepository;
import com.ay.exchange.common.util.PagingGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BoardContentService {
    private final BoardContentRepository boardContentRepository;

    public ModifiableBoardResponse findModifiableBoard(String email, Long boardId) {
        return boardContentRepository.findModifiableBoard(email, boardId);
    }

    public void save(Board board, String content) {
        BoardContent boardContent = BoardContent.builder()
                .content(content)
                .board(board)
                .build();
        boardContentRepository.save(boardContent);
    }

    public BoardContentInfo2Dto findBoardContentWithNoComments(Long boardId, String email) {
        return boardContentRepository.findBoardContentWithNoComments(boardId, PagingGenerator.getPageRequest(0), email);
    }

    public List<BoardContentInfoDto> findBoardContentWithComments(Long boardId, String email) {
        return boardContentRepository.findBoardContentWithComments(boardId, PagingGenerator.getPageRequest(0), email);
    }
}