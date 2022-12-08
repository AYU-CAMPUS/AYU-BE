package com.ay.exchange.board.service;

import com.ay.exchange.board.dto.response.BoardContentResponse;
import com.ay.exchange.board.repository.BoardContentRepository;
import com.ay.exchange.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardContentService {
    private final BoardContentRepository boardContentRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public BoardContentResponse getBoardContent(Long boardId, String token) {
        PageRequest pageRequest = PageRequest.of(0, 2,
                Sort.by(Sort.Direction.DESC, "id"));

        return boardContentRepository.findBoardContent(boardId, pageRequest, jwtTokenProvider.getUserId(token));
    }

    public Boolean checkModifiableBoard(String token, Long boardId) {
        boardContentRepository.checkModifiableBoard(jwtTokenProvider.getUserId(token), boardId);
        return true;
    }

}