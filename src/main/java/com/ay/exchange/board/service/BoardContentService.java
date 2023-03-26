package com.ay.exchange.board.service;

import com.ay.exchange.board.dto.query.BoardContentInfo2Dto;
import com.ay.exchange.board.dto.query.BoardContentInfoDto;
import com.ay.exchange.board.dto.response.ModifiableBoardResponse;
import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.BoardContent;
import com.ay.exchange.board.repository.BoardContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BoardContentService {
    private final BoardContentRepository boardContentRepository;
    private final int PAGE_LIMIT_LENGTH = 2;

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
        PageRequest pageRequest = PageRequest.of(0, PAGE_LIMIT_LENGTH,
                Sort.by(Sort.Direction.DESC, "id"));

        return boardContentRepository.findBoardContentWithNoComments(boardId, pageRequest, email);
    }

    public List<BoardContentInfoDto> findBoardContentWithComments(Long boardId, String email) {
        PageRequest pageRequest = PageRequest.of(0, PAGE_LIMIT_LENGTH,
                Sort.by(Sort.Direction.DESC, "id"));

        return boardContentRepository.findBoardContentWithComments(boardId, pageRequest, email);
    }
}