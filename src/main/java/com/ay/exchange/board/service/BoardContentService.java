package com.ay.exchange.board.service;

import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.dto.response.ModifiableBoardResponse;
import com.ay.exchange.board.entity.Board;
import com.ay.exchange.board.entity.BoardContent;
import com.ay.exchange.board.exception.NotFoundBoardException;
import com.ay.exchange.board.repository.BoardContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BoardContentService {
    private final BoardContentRepository boardContentRepository;
    private final int PAGE_LIMIT_LENGTH = 2;

    public BoardContentResponse getBoardContent(Long boardId, String email) {
        PageRequest pageRequest = PageRequest.of(0, PAGE_LIMIT_LENGTH,
                Sort.by(Sort.Direction.DESC, "id"));
        try {
            return boardContentRepository.findBoardContent(boardId, pageRequest, email);
        } catch (Exception e) {
            throw new NotFoundBoardException();
        }
    }

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
}